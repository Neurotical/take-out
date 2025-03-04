package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)) {
            date = date.plusDays(1);
            dateList.add(date);
        }

        List<Double> sumList = new ArrayList<>();
        for (LocalDate date1 : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date1, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date1, LocalTime.MAX);

            Map mp = new HashMap();
            mp.put("begin", beginTime);
            mp.put("end", endTime);
            mp.put("status", Orders.COMPLETED);
            Double sum = orderMapper.getByMap(mp);
            if (sum == null) sum = 0.0;
            sumList.add(sum);
        }


        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(sumList, ","));

        return turnoverReportVO;
    }
}
