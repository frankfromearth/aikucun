package com.akucun.checkbill.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akucun.checkbill.common.DateUtil;
import com.akucun.checkbill.common.constant.CheckBillApplyStatusEnum;
import com.akucun.checkbill.common.constant.CheckBillConstant;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;
import com.akucun.checkbill.dao.entity.User;
import com.akucun.checkbill.service.CheckBillApplyService;
import com.akucun.checkbill.service.UploadFileService;

/**
 * Created by zhaojin on 3/31/2018.
 */
public class CheckBillUserByMonthTask implements Callable{
    public static final Logger LOG= LoggerFactory.getLogger(CheckBillUserByMonthTask.class);

    private User user;
    private Date fromDate;
    private Date toDate;
    private String dateYearMonth;
    private String checkBillApplyId;
    private String tmpDir;

    private UploadFileService uploadFileService;
    private CheckBillApplyService checkBillApplyService;

    public void setUploadFileService(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }


    public void setCheckBillApplyService(CheckBillApplyService checkBillApplyService) {
        this.checkBillApplyService = checkBillApplyService;
    }

    public void setDateYearMonth(String dateYearMonth) {
        this.dateYearMonth = dateYearMonth;
    }

    public void setCheckBillApplyId(String checkBillApplyId) {
        this.checkBillApplyId = checkBillApplyId;
    }

    @Override
    public Object call() throws Exception
    {
        LOG.info("会员对账单申请 根据 月份 线程中 生成 用户{} 月份{} fromDate={},toDate={}对账 excel 文件 开始",user.getId(),dateYearMonth,fromDate,toDate);
        try{

            String filename=user.getUserNo()+"_"+user.getName()+"_"+dateYearMonth+"对账单.xlsx";
            File file=new File(tmpDir+File.separator+filename);

            LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 开始生成 excel文件{} ",user.getId(),dateYearMonth,file.getAbsolutePath());
            CheckBillApplyEntity res= genExcel(filename,file);
            LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 完成生成 excel文件{} ",user.getId(),dateYearMonth,file.getAbsolutePath());

            LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 开始上传文件{} ",user.getId(),dateYearMonth,file.getAbsolutePath());
            String url=uploadFileService.uploadFile(filename,file);
            LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 返回文件URL={} ",user.getId(),dateYearMonth,url);

            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setFileUrl(url);
            entity.setFinishTime(new Date());
            entity.setFileName(filename);
            entity.setStatus(CheckBillApplyStatusEnum.SUCESS.getStatus());
            entity.setCancelTotalAmount(res.getCancelTotalAmount());
            entity.setCancelTotalCount(res.getCancelTotalCount());
            entity.setPayTotalAmount(res.getPayTotalAmount());
            entity.setPayTotalCount(res.getPayTotalCount());
            //where 
            entity.setId(checkBillApplyId);
            LOG.info("会员对账单申请 根据 月份 更新checkBillApply :{}",entity);
            checkBillApplyService.updateById(entity);

            LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 删除临时文件{} ",user.getId(),dateYearMonth,file.getAbsolutePath());
            file.delete();

            LOG.info("会员对账单申请 根据 月份 线程中 生成 用户{} 月份{} 对账 excel 文件 成功 ",user.getId(),dateYearMonth);
        }catch (Exception e)
        {
            LOG.error("会员对账单申请 根据 月份 线程中 生成 用户{} 月份{} 对账 excel 文件 处理失败",user.getId(),dateYearMonth,e);

            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setStatus(CheckBillApplyStatusEnum.FAIL.getStatus());
            //where 
            entity.setId(checkBillApplyId);
            LOG.info("会员对账单申请 根据 月份 更新状态 为失败 checkBillApplyId={}",checkBillApplyId);
            checkBillApplyService.updateById(entity);
        }
        return null;
    }

    /**
     *
     * @return 返回要相加的总数
     * @throws Exception
     */
    private CheckBillApplyEntity  genExcel(String filename,File file ) throws Exception
    {
        BigDecimal  cancelTotalAmount=BigDecimal.ZERO;
        BigDecimal  payTotalAmount=BigDecimal.ZERO;
        int  payTotalCount=0;
        int  cancelTotalCount=0;
        
        XSSFWorkbook workbook=new XSSFWorkbook();
        FileOutputStream out=new FileOutputStream(file,false);
        try
        {
            Sheet sheet = workbook.createSheet();
            String sheetName=filename.substring(0, filename.lastIndexOf("."));
            workbook.setSheetName(0, sheetName);
            writeRowHead(sheet);//生成表头
          

            CellStyle amtStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            amtStyle.setDataFormat(df.getFormat("#,#0.00")); //小数点后保留两位
           
            //读数据
            int pageNo=1;
            int rowNum=1;
            List<CheckBillMonthDetail> queryData=null; 
            do
            {
                LOG.info ("会员对账单申请 根据 月份 线程中 userId={}, dateYearMonth={} 查询第{}页  ",user.getId(),dateYearMonth,pageNo);
                queryData=checkBillApplyService.queryExcelDataByDateRangePage(user.getId(),fromDate,toDate,pageNo);
                
                CheckBillApplyEntity itemSum= writeRowData(sheet,amtStyle,queryData, rowNum);//生成数据
                cancelTotalCount+=itemSum.getCancelTotalCount();
                payTotalCount+=itemSum.getPayTotalCount();
                cancelTotalAmount=cancelTotalAmount.add(itemSum.getCancelTotalAmount());
                payTotalAmount=payTotalAmount.add(itemSum.getPayTotalAmount());
                
                pageNo++;
                rowNum+=queryData.size();
                
            }while(queryData.size() == CheckBillConstant.PAGE_SIZE);
            
            workbook.write(out); 
            
        }catch (Exception e)
        {
            LOG.error("生成Excel 出错",e);
            throw  e;
        }finally {
            workbook.close();
            out.close();
        }

        CheckBillApplyEntity res=new CheckBillApplyEntity();
        res.setPayTotalCount(payTotalCount);
        res.setPayTotalAmount(payTotalAmount);
        res.setCancelTotalCount(cancelTotalCount);
        res.setCancelTotalAmount(cancelTotalAmount);
        return res;
    }
    private void writeRowHead(Sheet sheet)
    {
        int default_width=sheet.getColumnWidth(1);//default_width=2048

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("订单ID");
        row.createCell(1).setCellValue("订单生成时间");
        sheet.setColumnWidth(1, default_width*2+100);

        row.createCell(2).setCellValue("支付时间");
        sheet.setColumnWidth(2, default_width*2+100);

        row.createCell(3).setCellValue("商品金额");
        row.createCell(4).setCellValue("运费金额");
        row.createCell(5).setCellValue("支付金额");
        row.createCell(6).setCellValue("商品件数");
        row.createCell(7).setCellValue("支付状态");
        row.createCell(8).setCellValue("支付方式");
        row.createCell(9).setCellValue("品牌");
        row.createCell(10).setCellValue("订单状态");
        row.createCell(11).setCellValue("配送订单号");
        sheet.setColumnWidth(11, default_width*4);

        row.createCell(12).setCellValue("备注");
        sheet.setColumnWidth(12, default_width*2);

        row.createCell(13).setCellValue("商品描述");
        sheet.setColumnWidth(13, default_width*4);

        row.createCell(14).setCellValue("尺码");
        sheet.setColumnWidth(14, default_width*2);

        row.createCell(15).setCellValue("结算价");
        row.createCell(16).setCellValue("商品状态");
        row.createCell(17).setCellValue("取消购买");




    }
    private CheckBillApplyEntity writeRowData(Sheet sheet,CellStyle amtStyle,List<CheckBillMonthDetail> dataList,int rowNum)
    {

        String processOrderId="firstRow";

        BigDecimal  cancelTotalAmount=BigDecimal.ZERO;
        BigDecimal  payTotalAmount=BigDecimal.ZERO;
        int  payTotalCount=0;
        int  cancelTotalCount=0;

        for(CheckBillMonthDetail detail:dataList)
        {
            Row row = sheet.createRow(rowNum++);
            if(
                    !(processOrderId.equals(detail.getOrderId()))
             )
            {
                row.createCell(0).setCellValue(detail.getOrderId());
                row.createCell(1).setCellValue(DateUtil.formateDateISO(detail.getCreateTime()));
                row.createCell(2).setCellValue(DateUtil.formateDateISO((detail.getPayTime())));

                Cell cell03=row.createCell(3, CellType.NUMERIC);
                cell03.setCellStyle(amtStyle);
                cell03.setCellValue(detail.getProductPrice().divide(BigDecimal.valueOf(100)).doubleValue());

                Cell cell04=row.createCell(4, CellType.NUMERIC);
                cell04.setCellStyle(amtStyle);
                cell04.setCellValue(detail.getCarriageFee().divide(BigDecimal.valueOf(100)).doubleValue());

                Cell cell05=row.createCell(5, CellType.NUMERIC);
                cell05.setCellStyle(amtStyle);
                cell05.setCellValue(detail.getPayPrice().divide(BigDecimal.valueOf(100)).doubleValue());

                row.createCell(6, CellType.NUMERIC).setCellValue(detail.getPieceNum());
                row.createCell(7).setCellValue(detail.getPayStatus());
                row.createCell(8).setCellValue(detail.getPayType());
                row.createCell(9).setCellValue(detail.getBrand());
                row.createCell(10).setCellValue(detail.getOrderStatus());
                processOrderId=detail.getOrderId();
            }else
            {
                row.createCell(0).setCellValue("");
                row.createCell(1).setCellValue("");
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue("");
                row.createCell(7).setCellValue("");
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
            }
            row.createCell(11).setCellValue(detail.getDeliveryOrderNo());
            row.createCell(12).setCellValue(detail.getMemo());
            row.createCell(13).setCellValue(detail.getProductDescription());
            row.createCell(14).setCellValue(detail.getSize());
            row.createCell(15).setCellValue(detail.getSettlePrice().doubleValue());
            row.createCell(16).setCellValue(detail.getProductStatus());
            row.createCell(17).setCellValue(detail.getCancleBuy());
            if("正常购买".equals(detail.getCancleBuy()))
            {
                payTotalAmount=payTotalAmount.add(detail.getSettlePrice());
                payTotalCount++;
            }else
            {
                cancelTotalAmount=cancelTotalAmount.add(detail.getSettlePrice());
                cancelTotalCount++;
            }
        }
        CheckBillApplyEntity res=new CheckBillApplyEntity();
        res.setCancelTotalAmount(cancelTotalAmount);
        res.setCancelTotalCount(cancelTotalCount);
        res.setPayTotalAmount(payTotalAmount);
        res.setPayTotalCount(payTotalCount);
        return res;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("user", user)
                .append("fromDate", fromDate)
                .append("toDate", toDate)
                .append("dateYearMonth", dateYearMonth)
                .append("checkBillApplyId", checkBillApplyId)
                .append("tmpDir", tmpDir)
                .toString();
    }
}

