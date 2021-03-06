package io.finer.erp.jeecg.stock.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import io.finer.erp.jeecg.stock.entity.StkInventory;
import io.finer.erp.jeecg.stock.service.IStkInventoryService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.jeecg.stock.entity.StkCheckBillEntry;
import io.finer.erp.jeecg.stock.entity.StkCheckBill;
import io.finer.erp.jeecg.stock.vo.StkCheckBillPage;
import io.finer.erp.jeecg.stock.service.IStkCheckBillService;
import io.finer.erp.jeecg.stock.service.IStkCheckBillEntryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: ?????????
 * @Author: jeecg-boot
 * @Date:   2020-05-18
 * @Version: V1.0
 */
@Api(tags="?????????")
@RestController
@RequestMapping("/stock/stkCheckBill")
@Slf4j
public class StkCheckBillController {
	@Autowired
	private IStkCheckBillService stkCheckBillService;
	@Autowired
	private IStkCheckBillEntryService stkCheckBillEntryService;
	@Autowired
	private IStkInventoryService stkInventoryService;
	
	/**
	 * ??????????????????
	 *
	 * @param stkCheckBill
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "?????????-??????????????????")
	@ApiOperation(value="?????????-??????????????????", notes="?????????-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StkCheckBill stkCheckBill,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StkCheckBill> queryWrapper = QueryGenerator.initQueryWrapper(stkCheckBill, req.getParameterMap());
		Page<StkCheckBill> page = new Page<StkCheckBill>(pageNo, pageSize);
		IPage<StkCheckBill> pageList = stkCheckBillService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   ??????
	 *
	 * @param stkCheckBillPage
	 * @return
	 */
	@AutoLog(value = "?????????-??????")
	@ApiOperation(value="?????????-??????", notes="?????????-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StkCheckBillPage stkCheckBillPage) {
		StkCheckBill stkCheckBill = new StkCheckBill();
		BeanUtils.copyProperties(stkCheckBillPage, stkCheckBill);
		stkCheckBillService.saveMain(stkCheckBill, stkCheckBillPage.getStkCheckBillEntryList());
		return Result.ok("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param stkCheckBillPage
	 * @return
	 */
	@AutoLog(value = "?????????-??????")
	@ApiOperation(value="?????????-??????", notes="?????????-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StkCheckBillPage stkCheckBillPage) {
		StkCheckBill stkCheckBill = new StkCheckBill();
		BeanUtils.copyProperties(stkCheckBillPage, stkCheckBill);
		StkCheckBill stkCheckBillEntity = stkCheckBillService.getById(stkCheckBill.getId());
		if(stkCheckBillEntity==null) {
			return Result.error("?????????????????????");
		}
		stkCheckBillService.updateMain(stkCheckBill, stkCheckBillPage.getStkCheckBillEntryList());
		return Result.ok("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "?????????-??????id??????")
	@ApiOperation(value="?????????-??????id??????", notes="?????????-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		stkCheckBillService.delMain(id);
		return Result.ok("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "?????????-????????????")
	@ApiOperation(value="?????????-????????????", notes="?????????-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stkCheckBillService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.ok("?????????????????????");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "?????????-??????id??????")
	@ApiOperation(value="?????????-??????id??????", notes="?????????-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StkCheckBill stkCheckBill = stkCheckBillService.getById(id);
		if(stkCheckBill==null) {
			return Result.error("?????????????????????");
		}
		return Result.ok(stkCheckBill);
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????????????????ID??????")
	@ApiOperation(value="????????????ID??????", notes="??????-?????????ID??????")
	@GetMapping(value = "/queryStkCheckBillEntryByMainId")
	public Result<?> queryStkCheckBillEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		List<StkCheckBillEntry> stkCheckBillEntryList = stkCheckBillEntryService.selectByMainId(id);
		return Result.ok(stkCheckBillEntryList);
	}

	 @AutoLog(value = "??????????????????????????????")
	 @ApiOperation(value="??????????????????????????????", notes="??????-????????????????????????")
	 @GetMapping(value = "/queryStkCheckBillEntryByRange")
	 public Result<?> queryStkCheckBillEntryListByRange(StkCheckBill stkCheckBill, HttpServletRequest req) {
		 QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("is_closed", 0);

		 String sql =   "SELECT id FROM bas_warehouse %s";
		 String where = " WHERE `code` LIKE (SELECT CONCAT(`code`, '%%') FROM bas_warehouse WHERE id = '%s')";
		 String id = stkCheckBill.getWarehouseId();
		 where = (id == null || id.isEmpty()) ? "" : String.format(where, id);
		 queryWrapper.inSql("warehouse_id", String.format(sql, where));

		 sql =   "SELECT m.id " +
			     "  FROM bas_material_category c JOIN bas_material m ON c.id = m.category_id ";
		 where = " WHERE c.code LIKE (SELECT CONCAT(`code`, '%%') FROM bas_material_category WHERE id = '%s')";
		 id = stkCheckBill.getMaterialCategoryId();
		 where = (id == null || id.isEmpty()) ? "" : String.format(where, id);
		 queryWrapper.inSql("material_id", String.format(sql, where));

		 List<StkInventory> invList = stkInventoryService.list(queryWrapper);
		 List<StkCheckBillEntry> checkList = new ArrayList<>();
		 int i = 1;
		 for(StkInventory inv:invList) {
			 StkCheckBillEntry check = new StkCheckBillEntry();
			 BeanUtils.copyProperties(inv, check,
					 "id", "qty", "remark", "remark2", "remark3", "version");
			 check.setIsNewBatch(0);
			 check.setBookQty(inv.getQty());
			 check.setEntryNo(i++);
			 checkList.add(check);
		 }
		 return Result.ok(checkList);
	 }

    /**
    * ??????excel
    *
    * @param request
    * @param stkCheckBill
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StkCheckBill stkCheckBill) {
      // Step.1 ??????????????????????????????
      QueryWrapper<StkCheckBill> queryWrapper = QueryGenerator.initQueryWrapper(stkCheckBill, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 ??????????????????
      List<StkCheckBill> queryList = stkCheckBillService.list(queryWrapper);
      // ??????????????????
      String selections = request.getParameter("selections");
      List<StkCheckBill> stkCheckBillList = new ArrayList<StkCheckBill>();
      if(oConvertUtils.isEmpty(selections)) {
          stkCheckBillList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          stkCheckBillList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 ??????pageList
      List<StkCheckBillPage> pageList = new ArrayList<StkCheckBillPage>();
      for (StkCheckBill main : stkCheckBillList) {
          StkCheckBillPage vo = new StkCheckBillPage();
          BeanUtils.copyProperties(main, vo);
          List<StkCheckBillEntry> stkCheckBillEntryList = stkCheckBillEntryService.selectByMainId(main.getId());
          vo.setStkCheckBillEntryList(stkCheckBillEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi ??????Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "???????????????");
      mv.addObject(NormalExcelConstants.CLASS, StkCheckBillPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("???????????????", "?????????:"+sysUser.getRealname(), "?????????"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// ????????????????????????
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<StkCheckBillPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StkCheckBillPage.class, params);
              for (StkCheckBillPage page : list) {
                  StkCheckBill po = new StkCheckBill();
                  BeanUtils.copyProperties(page, po);
                  stkCheckBillService.saveMain(po, page.getStkCheckBillEntryList());
              }
              return Result.ok("?????????????????????????????????:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("??????????????????:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("?????????????????????");
    }

	 @AutoLog(value = "???????????????-??????id??????")
	 @ApiOperation(value="???????????????-??????id??????", notes="???????????????-??????id??????")
	 @PutMapping(value = "/approve")
	 public Result<?> approve(@RequestBody JSONObject json) {
		 stkCheckBillService.approve(json.getString("id"));
		 return Result.ok("????????????!");
	 }

 }
