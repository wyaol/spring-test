package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RsService {
    final RsEventRepository rsEventRepository;
    final UserRepository userRepository;
    final VoteRepository voteRepository;
    final TradeRepository tradeRepository;

    public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.tradeRepository = tradeRepository;
    }

    public void vote(Vote vote, int rsEventId) {
        Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
        Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
        if (!rsEventDto.isPresent()
                || !userDto.isPresent()
                || vote.getVoteNum() > userDto.get().getVoteNum()) {
            throw new RuntimeException();
        }
        VoteDto voteDto =
                VoteDto.builder()
                        .localDateTime(vote.getTime())
                        .num(vote.getVoteNum())
                        .rsEvent(rsEventDto.get())
                        .user(userDto.get())
                        .build();
        voteRepository.save(voteDto);
        UserDto user = userDto.get();
        user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
        userRepository.save(user);
        RsEventDto rsEvent = rsEventDto.get();
        rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
        rsEventRepository.save(rsEvent);
    }

    @Transactional
    public void buy(Trade trade, int id) throws Exception {
        trade.setEventId(id);
        TradeDto tradeDto = TradeDto.builder().rank(trade.getRank()).userId(trade.getUserId()).amount(trade.getAmount()).eventId(trade.getEventId()).build();
        List<TradeDto> tradeDtos = tradeRepository.findAllByRankOrderByAmountDesc(trade.getRank());
        if (tradeDtos.size() == 0) {
            tradeRepository.save(tradeDto);
        } else {
            TradeDto oldTrad = tradeDtos.get(0);
            if (trade.getAmount() <= oldTrad.getAmount())
                throw new Exception("金钱不足 目前该排行的购买金额为" + String.valueOf(oldTrad.getAmount()));
            else {
                tradeRepository.save(tradeDto);

                // 获取该排行旧热搜
                RsEventDto rsEventDtoOld = getRsEventDto(oldTrad.getEventId());

                // 删除旧热搜
                rsEventRepository.deleteById(oldTrad.getEventId());

                // 获取要购买的热搜
                RsEventDto rsEventDto = getRsEventDto(trade.getEventId());

                // 更新该热搜的投票数和被替换的一样
                rsEventDto.setVoteNum(rsEventDtoOld.getVoteNum());
                rsEventRepository.save(rsEventDto);
            }
        }
    }

    private RsEventDto getRsEventDto(Integer eventId) throws Exception {
        Optional<RsEventDto> res = rsEventRepository.findById(eventId);
        if (!res.isPresent()) throw new Exception("该热搜不存在");
        return res.get();
    }
}
