package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        UserReportVO userReportVO = new UserReportVO();

        //获取日期
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)) {
            date = date.plusDays(1);
            dateList.add(date);
        }
        userReportVO.setDateList(StringUtils.join(dateList, ","));

        //获取每日新增用户与总用户数
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date1 : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date1, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date1, LocalTime.MAX);
            Map mp = new HashMap();
            mp.put("end", endTime);
            Integer totalUser = userMapper.getByMap(mp);
            totalUserList.add(totalUser);

            mp.put("begin", beginTime);

            Integer newUser = userMapper.getByMap(mp);
            newUserList.add(newUser);
        }
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));
        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));

        return userReportVO;
    }

    /**
     * 统计订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        //获取日期
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)) {
            date = date.plusDays(1);
            dateList.add(date);
        }

        //获取区间内订单总数及有效订单总数
        List<Integer> validOrderList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();

        for (LocalDate date1 : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date1, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date1, LocalTime.MAX);

            Map mp = new HashMap();
            mp.put("begin", beginTime);
            mp.put("end", endTime);
            Integer totalOrders = orderMapper.countByMap(mp);
            mp.put("status", Orders.COMPLETED);
            Integer validOrders = orderMapper.countByMap(mp);

            totalOrderList.add(totalOrders);
            validOrderList.add(validOrders);
        }


        //获取订单总数
        Integer totalOrdersNum = totalOrderList.stream().reduce(Integer::sum).get();
        Integer validOrdersNum = validOrderList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrdersNum != 0){
            orderCompletionRate = validOrdersNum.doubleValue() / totalOrdersNum;
        }

        //封装
        orderReportVO.setDateList(StringUtils.join(dateList, ","));
        orderReportVO.setOrderCountList(StringUtils.join(totalOrderList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderList, ","));
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        orderReportVO.setTotalOrderCount(totalOrdersNum);
        orderReportVO.setValidOrderCount(validOrdersNum);

        return orderReportVO;
    }

    /**
     * 获取销量前10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();

        List<Integer> numberlist = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        salesTop10ReportVO.setNumberList(StringUtils.join(numberlist, ","));

        List<String> namelist = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        salesTop10ReportVO.setNameList(StringUtils.join(namelist, ","));

        return salesTop10ReportVO;
    }

    /**
     * 导出近30天营业数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //获取近30天营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);

        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        //将数据写入excel
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(in);

            //填充数据
            XSSFSheet sheet = workbook.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue("时间：从"+beginTime + "到" + endTime);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //写入具体数据
            for(int i=0;i<30;i++){
                LocalDate localDate = dateBegin.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));

                XSSFRow row1 = sheet.getRow(7 + i);
                row1.getCell(1).setCellValue(localDate.toString());
                row1.getCell(2).setCellValue(businessData1.getTurnover());
                row1.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row1.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row1.getCell(5).setCellValue(businessData1.getUnitPrice());
                row1.getCell(6).setCellValue(businessData1.getNewUsers());

            }

            //通过输出流将excel下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);

            out.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
