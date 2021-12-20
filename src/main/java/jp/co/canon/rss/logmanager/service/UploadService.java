package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.dto.upload.ResLocalJobFileIdx;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.repository.ErrorLogDownloadSettingRepository;
import jp.co.canon.rss.logmanager.repository.LocalJobFileIdVoRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasDataRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasDataSiteRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasItemMasterRepository;
import jp.co.canon.rss.logmanager.system.FileUploadDownloadService;
import jp.co.canon.rss.logmanager.util.ExcelReader;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadSettingVo;
import jp.co.canon.rss.logmanager.vo.LocalJobFileIdVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service()
public class UploadService {
    @Value("${cras-data.sheet-cras-data}")
    private String sheetCrasData;

    @Value("${cras-data.sheet-cras-item}")
    private String sheetCrasItem;

    @Value("${error-log-download.sheet-error-log-download}")
    private String sheetErrorLogDownload;

    @Value("${cras-data.sheet-cras-data-header}")
    private List<String> sheetCrasDataHeader;

    @Value("${cras-data.sheet-cras-item-master-header}")
    private List<String> sheetCrasItemHeader;

    @Value("${error-log-download.sheet-error-log-download-header}")
    private List<String> sheetErrorLogDownloadHeader;

    LocalJobFileIdVoRepository localJobFileIdVoRepository;
    FileUploadDownloadService fileUploadDownloadService;
    SiteRepository siteRepository;
    CrasDataRepository crasDataRepository;
    CrasItemMasterRepository crasItemMasterRepository;
    CrasDataSiteRepository crasDataSiteRepository;
    ErrorLogDownloadSettingRepository errorLogDownloadSettingRepository;
    ExcelReader excelReader = new ExcelReader();

    public UploadService(LocalJobFileIdVoRepository localJobFileIdVoRepository,
                         FileUploadDownloadService fileUploadDownloadService,
                         SiteRepository siteRepository,
                         CrasDataRepository crasDataRepository,
                         CrasItemMasterRepository crasItemMasterRepository,
                         CrasDataSiteRepository crasDataSiteRepository,
                         ErrorLogDownloadSettingRepository errorLogDownloadSettingRepository) {
        this.localJobFileIdVoRepository = localJobFileIdVoRepository;
        this.fileUploadDownloadService = fileUploadDownloadService;
        this.siteRepository = siteRepository;
        this.crasDataRepository = crasDataRepository;
        this.crasItemMasterRepository = crasItemMasterRepository;
        this.crasDataSiteRepository = crasDataSiteRepository;
        this.errorLogDownloadSettingRepository = errorLogDownloadSettingRepository;
    }

    public ResLocalJobFileIdx uploadLocalJobFile(MultipartFile file) throws FileUploadException {
        final String format = "%s_%s";
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String savedFileName = String.format(format, currentTime, file.getOriginalFilename());  // 20210518_filename.zip
        fileUploadDownloadService.storeLocalFile(file, savedFileName);

        LocalJobFileIdVo localJobFileIdVo = new LocalJobFileIdVo()
                .setUploadDate(LocalDateTime.now())
                .setFileName(savedFileName)
                .setFileOriginalName(file.getOriginalFilename());

        ResLocalJobFileIdx resLocalJobFileIdx = new ResLocalJobFileIdx()
                .setFileIndex(localJobFileIdVoRepository.save(localJobFileIdVo).getId());

        return resLocalJobFileIdx;
    }

    public Boolean crasDataImport(File file, int crasDataSiteId) throws StatusResourceNotFoundException {
        List<CrasDataVo> crasDataVoDeleteList = new ArrayList<>();
        List<CrasItemMasterVo> crasItemMasterVoDeleteList = new ArrayList<>();
        Boolean result = false;
        try {
            XSSFWorkbook excelReaderResult = excelReader.readExcel(file);
            List<CrasDataVo> crasDataVoList = new ArrayList<>();
            List<CrasItemMasterVo> crasItemMasterVoList = new ArrayList<>();

            CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Boolean checkUserFabName = false;

            for (int idx = 0; idx < excelReaderResult.getNumberOfSheets(); idx++) {
                XSSFSheet sheet = excelReaderResult.getSheetAt(idx);

                for (int rowindex = 1; rowindex < sheet.getPhysicalNumberOfRows(); rowindex++) {
                    CrasDataVo crasDataVo = new CrasDataVo();
                    CrasItemMasterVo crasItemMasterVo = new CrasItemMasterVo();

                    XSSFRow row = sheet.getRow(rowindex);

                    if(crasDataSiteVo.getCrasDataSiteVo().getCrasCompanyName().equals(String.valueOf(row.getCell(1)))
                            && crasDataSiteVo.getCrasDataSiteVo().getCrasFabName().equals(String.valueOf(row.getCell(2)))) {
                        checkUserFabName = true;

                        Optional<ResSitesDetailDTO> sitesInfo = siteRepository.findByCrasCompanyNameIgnoreCaseAndCrasFabNameIgnoreCase(
                                String.valueOf(row.getCell(1)), String.valueOf(row.getCell(2)));

                        if (sitesInfo.isPresent()) {
                            if (sheet.getSheetName().equals(sheetCrasData)) {
                                String getCell9 = row.getCell(9).toString();
                                if (getCell9.isEmpty() || getCell9.length() == 0)
                                    getCell9 = null;
                                crasDataVo.setSiteId(crasDataSiteId)
                                        .setUserName(row.getCell(1) != null ? String.valueOf(row.getCell(1)) : "")
                                        .setFabName(row.getCell(2) != null ? String.valueOf(row.getCell(2)) : "")
                                        .setItemName(row.getCell(3) != null ? String.valueOf(row.getCell(3)) : "")
                                        .setTargetTable(row.getCell(4) != null ? String.valueOf(row.getCell(4)) : "")
                                        .setTargetCol1(row.getCell(5) != null ? String.valueOf(row.getCell(5)) : "")
                                        .setTargetCol2(row.getCell(6) != null ? String.valueOf(row.getCell(6)) : "")
                                        .setOperations(row.getCell(7) != null ? String.valueOf(row.getCell(7)) : "")
                                        .setCalPeriodUnit(row.getCell(8) != null ? String.valueOf(row.getCell(8)) : "")
                                        .setCoef(getCell9 != null ? Double.parseDouble(row.getCell(9).toString()) : 0)
                                        .setGroupCol(row.getCell(10) != null ? String.valueOf(row.getCell(10)) : "")
                                        .setManualWhere(row.getCell(11) != null ? String.valueOf(row.getCell(11)) : "")
                                        .setCalResultType(row.getCell(12) != null ? String.valueOf(row.getCell(12)) : "")
                                        .setComments(row.getCell(13) != null ? String.valueOf(row.getCell(13)) : "")
                                        .setEnable(String.valueOf(row.getCell(14)).equals("TRUE") ? true : false)
                                        .setCrasDataSiteVo(crasDataSiteVo);
                                crasDataVoList.add(crasDataVo);
                            } else if (sheet.getSheetName().equals(sheetCrasItem)) {
                                String getCell4 = row.getCell(4).toString();
                                String getCell6 = row.getCell(6).toString();
                                if (getCell4.isEmpty() || getCell4.length() == 0)
                                    getCell4 = null;
                                if (getCell6.isEmpty() || getCell6.length() == 0)
                                    getCell6 = null;

                                crasItemMasterVo.setSiteId(crasDataSiteId)
                                        .setUserName(row.getCell(1) != null ? String.valueOf(row.getCell(1)) : "")
                                        .setFabName(row.getCell(2) != null ? String.valueOf(row.getCell(2)) : "")
                                        .setItemName(row.getCell(3) != null ? String.valueOf(row.getCell(3)) : "")
                                        .setCalRange(getCell4 != null ? row.getCell(4).getNumericCellValue() : 0)
                                        .setCalCondition(row.getCell(5) != null ? String.valueOf(row.getCell(5)) : "")
                                        .setThreshold(getCell6 != null ? row.getCell(6).getNumericCellValue() : 0)
                                        .setCompare(row.getCell(7) != null ? String.valueOf(row.getCell(7)) : "")
                                        .setTitle(row.getCell(8) != null ? String.valueOf(row.getCell(8)) : "")
                                        .setDescription(row.getCell(9) != null ? String.valueOf(row.getCell(9)) : "")
                                        .setEnable(String.valueOf(row.getCell(10)).equals("TRUE") ? true : false)
                                        .setUnit(row.getCell(11) != null ? String.valueOf(row.getCell(11)) : "")
                                        .setCrasDataSiteVoItem(crasDataSiteVo);
                                crasItemMasterVoList.add(crasItemMasterVo);
                            }
                        }
                    }
                }
            }

            if(checkUserFabName == false)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            crasDataVoDeleteList = Optional
                    .ofNullable(crasDataRepository.findBySiteId(crasDataSiteId, Sort.by(Sort.Direction.DESC, "itemId")))
                    .orElse(Collections.emptyList());
            if (crasDataVoDeleteList.size() != 0)
                crasDataRepository.deleteAll(crasDataVoDeleteList);

            List<CrasDataVo> crasDataVoSave = crasDataRepository.saveAll(crasDataVoList);
            if (crasDataVoSave.size() == 0) {
                crasDataRepository.saveAll(crasDataVoDeleteList);
                result = true;
            }

            crasItemMasterVoDeleteList = Optional
                    .ofNullable(crasItemMasterRepository.findBySiteId(crasDataSiteId, Sort.by(Sort.Direction.DESC, "itemId")))
                    .orElse(Collections.emptyList());
            if (crasItemMasterVoDeleteList.size() != 0)
                crasItemMasterRepository.deleteAll(crasItemMasterVoDeleteList);

            List<CrasItemMasterVo> crasItemMasterVoSave = crasItemMasterRepository.saveAll(crasItemMasterVoList);
            if (crasItemMasterVoSave.size() == 0) {
                crasItemMasterRepository.saveAll(crasItemMasterVoDeleteList);
                result = true;
            }

            return result;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void crasDataExport(int crasDataSiteId, HttpServletResponse response, String crasFileName) throws EncryptedDocumentException {
        try {
            List<CrasDataVo> crasDataVoList = Optional
                    .ofNullable(crasDataRepository.findBySiteId(crasDataSiteId, Sort.by(Sort.Direction.DESC, "itemId")))
                    .orElse(Collections.emptyList());
            List<CrasItemMasterVo> crasItemMasterVoList = Optional
                    .ofNullable(crasItemMasterRepository.findBySiteId(crasDataSiteId, Sort.by(Sort.Direction.DESC, "itemId")))
                    .orElse(Collections.emptyList());

            // Save Excel File
            SXSSFWorkbook wb = new SXSSFWorkbook();

            for(int idx=0; idx<2; idx++) {
                String sheetName = null;
                List<String> headerNames = null;
                SXSSFRow row = null;
                SXSSFCell cell = null;
                int rowNum = 0;

                if(idx==0) {
                    sheetName = sheetCrasData;
                    headerNames = sheetCrasDataHeader;
                }
                else if(idx==1) {
                    sheetName = sheetCrasItem;
                    headerNames = sheetCrasItemHeader;
                }

                SXSSFSheet sheet = wb.createSheet(sheetName);
                sheet.setRandomAccessWindowSize(100);

                row = sheet.createRow(rowNum++);
                int headerLine = 0;
                for(String headerName : headerNames) {
                    cell = row.createCell(headerLine);
                    cell.setCellValue(headerName);
                    headerLine++;
                }

                if(idx==0) {
                    for (CrasDataVo crasDataVo : crasDataVoList) {
                        row = sheet.createRow(rowNum++);
                        cell = row.createCell(0);
                        cell.setCellValue(crasDataVo.getItemId());
                        cell = row.createCell(1);
                        cell.setCellValue(crasDataVoList.get(0).getCrasDataSiteVo().getCrasDataSiteVo().getCrasCompanyName());
                        cell = row.createCell(2);
                        cell.setCellValue(crasDataVoList.get(0).getCrasDataSiteVo().getCrasDataSiteVo().getCrasFabName());
                        cell = row.createCell(3);
                        cell.setCellValue(crasDataVo.getItemName());
                        cell = row.createCell(4);
                        cell.setCellValue(crasDataVo.getTargetTable());
                        cell = row.createCell(5);
                        cell.setCellValue(crasDataVo.getTargetCol1());
                        cell = row.createCell(6);
                        cell.setCellValue(crasDataVo.getTargetCol2());
                        cell = row.createCell(7);
                        cell.setCellValue(crasDataVo.getOperations());
                        cell = row.createCell(8);
                        cell.setCellValue(crasDataVo.getCalPeriodUnit());
                        cell = row.createCell(9);
                        cell.setCellValue(crasDataVo.getCoef());
                        cell = row.createCell(10);
                        cell.setCellValue(crasDataVo.getGroupCol());
                        cell = row.createCell(11);
                        cell.setCellValue(crasDataVo.getManualWhere());
                        cell = row.createCell(12);
                        cell.setCellValue(crasDataVo.getCalResultType());
                        cell = row.createCell(13);
                        cell.setCellValue(crasDataVo.getComments());
                        cell = row.createCell(14);
                        cell.setCellValue(crasDataVo.getEnable());
                    }
                    sheet.flushRows(crasDataVoList.size());
                }
                else if(idx==1) {
                    for (CrasItemMasterVo crasItemMasterVo : crasItemMasterVoList) {
                        row = sheet.createRow(rowNum++);
                        cell = row.createCell(0);
                        cell.setCellValue(crasItemMasterVo.getItemId());
                        cell = row.createCell(1);
                        cell.setCellValue(crasDataVoList.get(0).getCrasDataSiteVo().getCrasDataSiteVo().getCrasCompanyName());
                        cell = row.createCell(2);
                        cell.setCellValue(crasDataVoList.get(0).getCrasDataSiteVo().getCrasDataSiteVo().getCrasFabName());
                        cell = row.createCell(3);
                        cell.setCellValue(crasItemMasterVo.getItemName());
                        cell = row.createCell(4);
                        cell.setCellValue(crasItemMasterVo.getCalRange());
                        cell = row.createCell(5);
                        cell.setCellValue(crasItemMasterVo.getCalCondition());
                        cell = row.createCell(6);
                        cell.setCellValue(crasItemMasterVo.getThreshold());
                        cell = row.createCell(7);
                        cell.setCellValue(crasItemMasterVo.getCompare());
                        cell = row.createCell(8);
                        cell.setCellValue(crasItemMasterVo.getTitle());
                        cell = row.createCell(9);
                        cell.setCellValue(crasItemMasterVo.getDescription());
                        cell = row.createCell(10);
                        cell.setCellValue(crasItemMasterVo.getEnable());
                        cell = row.createCell(11);
                        cell.setCellValue(crasItemMasterVo.getUnit());
                    }
                    sheet.flushRows(crasItemMasterVoList.size());
                }
            }
            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition",String.format("attachment; filename=\"%s\""
                    , URLEncoder.encode(crasFileName,"UTF-8")));

            wb.write(response.getOutputStream());
            wb.close();
            wb.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Boolean errorLogSettingDataImport(File file, int siteId) throws StatusResourceNotFoundException {
        List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoDeleteList;
        Boolean result = false;
        try {
            XSSFWorkbook excelReaderResult = excelReader.readExcel(file);
            List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoList = new ArrayList<>();

            for (int idx = 0; idx < excelReaderResult.getNumberOfSheets(); idx++) {
                XSSFSheet sheet = excelReaderResult.getSheetAt(idx);

                for (int rowindex = 1; rowindex < sheet.getPhysicalNumberOfRows(); rowindex++) {
                    ErrorLogDownloadSettingVo errorLogDownloadSettingVo = new ErrorLogDownloadSettingVo();

                    XSSFRow row = sheet.getRow(rowindex);

                    SiteVo siteVo = siteRepository.findById(siteId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                    if (sheet.getSheetName().equals(sheetErrorLogDownload)) {
                        String errorCode = String.valueOf(row.getCell(1));
                        String before = String.valueOf(row.getCell(4));
                        String after = String.valueOf(row.getCell(5));

                        errorLogDownloadSettingVo.setSiteId(siteId)
                                .setError_code(errorCode.contains(".") ? String.valueOf((int) row.getCell(1).getNumericCellValue()) : errorCode)
                                .setType(row.getCell(2) != null ? String.valueOf(row.getCell(2)) : "")
                                .setCommand(row.getCell(3) != null ? String.valueOf(row.getCell(3)) : "")
                                .setBefore(before.contains(".") ? String.valueOf((int) row.getCell(4).getNumericCellValue()) : before)
                                .setAfter(after.contains(".") ? String.valueOf((int) row.getCell(5).getNumericCellValue()) : after)
                                .setSiteVoList(siteVo);
                        errorLogDownloadSettingVoList.add(errorLogDownloadSettingVo);
                    }
                }
            }

            errorLogDownloadSettingVoDeleteList = Optional
                    .ofNullable(errorLogDownloadSettingRepository.findBySiteId(siteId, Sort.by(Sort.Direction.DESC, "id")))
                    .orElse(Collections.emptyList());
            if (errorLogDownloadSettingVoDeleteList != null)
                errorLogDownloadSettingRepository.deleteAll(errorLogDownloadSettingVoDeleteList);

            List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoSave =
                    errorLogDownloadSettingRepository.saveAll(errorLogDownloadSettingVoList);
            if(errorLogDownloadSettingVoSave.size() == 0) {
                errorLogDownloadSettingRepository.saveAll(errorLogDownloadSettingVoDeleteList);
                result = true;
            }

            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new StatusResourceNotFoundException(e.getMessage());
        }
    }

    public void errorLogSettingDataExport(int siteId, HttpServletResponse response, String fileName) throws EncryptedDocumentException {
        try {
            List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoList = Optional
                    .ofNullable(errorLogDownloadSettingRepository.findBySiteId(siteId, Sort.by(Sort.Direction.ASC, "id")))
                    .orElse(Collections.emptyList());

            ResSitesDetailDTO resultSitesDetail = siteRepository.findBySiteId(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            String companyName = resultSitesDetail.getCrasCompanyName();
            String fabName = resultSitesDetail.getCrasFabName();

            // Save Excel File
            SXSSFWorkbook wb = new SXSSFWorkbook();

            List<String> headerNames = sheetErrorLogDownloadHeader;
            SXSSFRow row = null;
            SXSSFCell cell = null;
            int rowNum = 0;

            SXSSFSheet sheet = wb.createSheet(sheetErrorLogDownload);
            sheet.setRandomAccessWindowSize(100);

            row = sheet.createRow(rowNum++);
            int headerLine = 0;
            for(String headerName : headerNames) {
                cell = row.createCell(headerLine);
                cell.setCellValue(headerName);
                headerLine++;
            }

            int idx = 0;
            for (ErrorLogDownloadSettingVo errorLogDownloadSettingVo : errorLogDownloadSettingVoList) {
                idx++;
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(idx);
                cell = row.createCell(1);
                cell.setCellValue(errorLogDownloadSettingVo.getError_code());
                cell = row.createCell(2);
                cell.setCellValue(errorLogDownloadSettingVo.getType());
                cell = row.createCell(3);
                cell.setCellValue(errorLogDownloadSettingVo.getCommand());
                cell = row.createCell(4);
                cell.setCellValue(errorLogDownloadSettingVo.getBefore());
                cell = row.createCell(5);
                cell.setCellValue(errorLogDownloadSettingVo.getAfter());
            }
            sheet.flushRows(errorLogDownloadSettingVoList.size());

            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition",String.format("attachment; filename=\"%s\""
                    , fileName.split("\\.")[0]
                            + "_" + companyName
                            + "_" + fabName
                            + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                            + "." + fileName.split("\\.")[1]));

            wb.write(response.getOutputStream());
            wb.close();
            wb.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
