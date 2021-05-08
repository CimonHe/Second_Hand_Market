package com.cimonhe.controller;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.*;
import com.cimonhe.service.*;
import com.cimonhe.utils.URLFileMap;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/showGoods")
@CrossOrigin
@Transactional
public class ShowGoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    Tag_GoodsService tag_goodsService;

    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    MsgService msgService;

    @Autowired
    OrderService orderService;

    @Autowired
    AddressInfoService addressInfoService;

    private final Logger logger = LoggerFactory.getLogger(getClass());//提供日志类


    @ApiOperation("根据页码大小返回待出售商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/allUnsoldGoodsPagesCount",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "待出售商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String allUnsoldGoodsPagesCount(@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        if (pageSize!=0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"获得待出售商品的页码数成功!!");
            logger.info("获得待出售商品的页码数成功");
            returnValue.put("allPagesCount",(goodsService.cntOnSaleGoods()+pageSize-1)/pageSize);
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }
        return returnValue.toString();
    }



    @ApiOperation("展示所有未出售商品接口")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/showAllUnsoldGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String showAllUnsoldGoods(@RequestParam int pageNum,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();

        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<List<String>> imagePaths = new ArrayList<>();
        List<String> sellerPhones = new ArrayList<>();
        List<List<Integer>> tagIds = new ArrayList<>();
        List<List<String>> tagNames = new ArrayList<>();

        PageHelper.startPage(pageNum,pageSize);

        for (Goods goods : goodsService.queryOnSaleGoods()) {
            goodsIds.add(goods.getId());
            goodsInfos.add(goods.getGoodsInfo());
            prices.add(goods.getPrice());
            sellerIds.add(goods.getSellerId());

            sellerNames.add(userService.queryUserById(goods.getSellerId()).getUsername());

            sellerPhones.add(goods.getSellerPhone());

            List<Integer> tagIdList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            for (Tag_Goods tag_goods : tag_goodsService.queryByGoodsId(goods.getId())) {
                tagIdList.add(tag_goods.getTagId());
                tagNameList.add(tagService.queryTagNameById(tag_goods.getTagId()));
            }
            tagIds.add(tagIdList);
            tagNames.add(tagNameList);
            imagePaths.add(URLFileMap.getGoodsImgPaths(goods.getId()));
        }

        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("prices",prices);
        returnValue.put("sellerIds",sellerIds);
        returnValue.put("sellerNames",sellerNames);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("sellerPhones",sellerPhones);
        returnValue.put("tagIds",tagIds);
        returnValue.put("tagNames",tagNames);

        return returnValue.toString();
    }

    @ApiOperation("展示单个商品详情页")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsId:商品id goodsInfo:商品信息 price:价格 sellerId:卖家id  sellerName:卖家名字列表 imagePaths:商品图列表 sellerPhone:卖家联系方式 tagIds:标签id列表 tagNames:标签名列表"),
    })
    @PostMapping(value = "/showSingleUnsoldGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
    })
    public String showSingleUnsoldGoods(@RequestParam int goodsId ){
        JSONObject returnValue = new JSONObject();
        List<String> imagePaths;
        List<Integer> tagIds = new ArrayList<>();
        List<String> tagNames = new ArrayList<>();
        Goods goods = goodsService.queryById(goodsId);
        returnValue.put("goodsId",goods.getId());
        returnValue.put("goodsInfo",goods.getGoodsInfo());
        returnValue.put("price",goods.getPrice());
        returnValue.put("sellerId",goods.getSellerId());
        returnValue.put("sellerName",userService.queryUserById(goods.getSellerId()).getUsername());

        for (Tag_Goods tag_goods : tag_goodsService.queryByGoodsId(goods.getId())) {
            tagIds.add(tag_goods.getTagId());
            tagNames.add(tagService.queryTagNameById(tag_goods.getTagId()));
        }
        imagePaths = URLFileMap.getGoodsImgPaths(goods.getId());
        returnValue.put("tagIds",tagIds);
        returnValue.put("tagNames",tagNames);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("sellerPhone",goods.getSellerPhone());

        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回关键字搜索待出售商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/unsoldGoodsCount",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "搜索待出售商品页的大小", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "goodsInfo", value = "搜索商品信息", required = true, dataType = "String",paramType="query"),
    })
    public String unsoldGoodsCount(@RequestParam int pageSize,@RequestParam String goodsInfo){
        JSONObject returnValue = new JSONObject();
        returnValue.put("allPagesCount",(goodsService.cntOnSaleByInfoLike(goodsInfo)+pageSize-1)/pageSize);
        return returnValue.toString();
    }

    @ApiOperation("用关键字搜索待出售商品")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/searchUnsoldGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "搜索商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "搜索商品页的大小", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "goodsInfo", value = "搜索商品信息", required = true, dataType = "String",paramType="query"),
    })
    public String searchUnsoldGoods(@RequestParam int pageNum,@RequestParam int pageSize,@RequestParam String goodsInfo ){
        JSONObject returnValue = new JSONObject();
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<List<String>> imagePaths = new ArrayList<>();
        List<String> sellerPhones = new ArrayList<>();
        List<List<Integer>> tagIds = new ArrayList<>();
        List<List<String>> tagNames = new ArrayList<>();

        PageHelper.startPage(pageNum,pageSize);

        for (Goods goods : goodsService.queryByInfoLike_IOS_PS(goodsInfo)) {
            goodsIds.add(goods.getId());
            goodsInfos.add(goods.getGoodsInfo());
            prices.add(goods.getPrice());
            sellerIds.add(goods.getSellerId());

            sellerNames.add(userService.queryUserById(goods.getSellerId()).getUsername());

            sellerPhones.add(goods.getSellerPhone());

            List<Integer> tagIdList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            for (Tag_Goods tag_goods : tag_goodsService.queryByGoodsId(goods.getId())) {
                tagIdList.add(tag_goods.getTagId());
                tagNameList.add(tagService.queryTagNameById(tag_goods.getTagId()));
            }
            tagIds.add(tagIdList);
            tagNames.add(tagNameList);
            imagePaths.add(URLFileMap.getGoodsImgPaths(goods.getId()));
        }

        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("prices",prices);
        returnValue.put("sellerIds",sellerIds);
        returnValue.put("sellerNames",sellerNames);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("sellerPhones",sellerPhones);
        returnValue.put("tagIds",tagIds);
        returnValue.put("tagNames",tagNames);

        return returnValue.toString();
    }



    @ApiOperation("通过标签id获得标签内容")
    @ApiResponses({
            @ApiResponse(code=200,message="tagName:标签名"),
    })
    @PostMapping(value = "/getTagById",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "int",paramType="query"),
    })
    public String getTagById(@RequestParam int tagId){
        JSONObject returnValue=new JSONObject();

        returnValue.put("tagName",tagService.queryTagNameById(tagId));

        return returnValue.toString();
    }

    @ApiOperation("获得所有标签及其id")
    @ApiResponses({
            @ApiResponse(code=200,message="tags:标签(包括 tagId:标签ID tagName:标签名)"),
    })
    @RequestMapping(value = "/getAllTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String getAllTag(){
        JSONObject returnValue=new JSONObject();

        returnValue.put("tags",tagService.queryAllTag());

        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回根据标签Id搜索的商品数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/cntGoodsByTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "待出售商品页的大小", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "tagId", value = "标签Id", required = true, dataType = "int",paramType="query")
    })
    public String cntGoodsByTag(@RequestParam int pageSize,@RequestParam int tagId){
        JSONObject returnValue = new JSONObject();

        if (pageSize!=0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"获得待出售商品的页码数成功!!");
            returnValue.put("allPagesCount",((goodsService.cntByTagId(tagId))+pageSize-1)/pageSize);
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }

        return returnValue.toString();
    }

    @ApiOperation("用关键字搜索待出售商品")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/queryGoodsByTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "搜索商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "搜索商品页的大小", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "tagId", value = "搜索商品信息", required = true, dataType = "int",paramType="query"),
    })
    public String queryGoodsByTag(@RequestParam int pageNum,@RequestParam int pageSize,@RequestParam int tagId){
        JSONObject returnValue = new JSONObject();
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<List<String>> imagePaths = new ArrayList<>();
        List<String> sellerPhones = new ArrayList<>();
        List<List<Integer>> tagIds = new ArrayList<>();
        List<List<String>> tagNames = new ArrayList<>();

        PageHelper.startPage(pageNum,pageSize);

        for (Goods goods : goodsService.queryByTagId(tagId)) {
            goodsIds.add(goods.getId());
            goodsInfos.add(goods.getGoodsInfo());
            prices.add(goods.getPrice());
            sellerIds.add(goods.getSellerId());

            sellerNames.add(userService.queryUserById(goods.getSellerId()).getUsername());

            sellerPhones.add(goods.getSellerPhone());

            List<Integer> tagIdList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            for (Tag_Goods tag_goods : tag_goodsService.queryByGoodsId(goods.getId())) {
                tagIdList.add(tag_goods.getTagId());
                tagNameList.add(tagService.queryTagNameById(tag_goods.getTagId()));
            }
            tagIds.add(tagIdList);
            tagNames.add(tagNameList);
            imagePaths.add(URLFileMap.getGoodsImgPaths(goods.getId()));
        }

        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("prices",prices);
        returnValue.put("sellerIds",sellerIds);
        returnValue.put("sellerNames",sellerNames);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("sellerPhones",sellerPhones);
        returnValue.put("tagIds",tagIds);
        returnValue.put("tagNames",tagNames);

       return returnValue.toString();
    }


    @ApiOperation("返回某商品所有留言")
    @ApiResponses({
            @ApiResponse(code=200,message="contents: 内容列表 dates:留言时间  criticIds: 留言用户id列表 criticNames: 留言用户名字列表"),
    })
    @PostMapping(value = "/queryAllMsgByGoodId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
    })
    public String queryAllMsgByGoodId(@RequestParam int goodsId){
        JSONObject returnValue = new JSONObject();
        List<Msg> msgs =  msgService.queryMsgByGoodsId(goodsId);
        List<String> contents = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        List<Integer> criticIds = new ArrayList<>();
        List<String> criticNames = new ArrayList<>();
        returnValue.put("Msgs",msgs);
        for (Msg msg : msgs) {
            contents.add(msg.getContent());
            dates.add(msg.getDate());
            criticIds.add(msg.getCriticId());
            criticNames.add(userService.queryUserById(msg.getCriticId()).getUsername());
        }
        returnValue.put("contents",contents);
        returnValue.put("dates",dates);
        returnValue.put("criticIds",criticIds);
        returnValue.put("criticNames",criticNames);
        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回该用户出售的订单的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/cntOrderBySellerId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "审核商品页的大小", required = true, dataType = "int",paramType="query"),
//            @ApiImplicitParam(name = "sellerId", value = "卖家id", required = true, dataType = "int",paramType="query")
    })
    public String cntOrderBySellerId(@RequestParam int sellerId,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        if (pageSize==0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"获得出售的订单的页码成功!!");
            returnValue.put("allPagesCount",(orderService.cntOrderBySellerId(sellerId)+pageSize-1)/pageSize);
        }
        return returnValue.toString();
    }

    @ApiOperation("根据用户id查询其售出的订单:")
    @ApiResponses({
            @ApiResponse(code=200,message="orderIds:订单列表 goodsIds:商品id列表,goodsInfos:商品信息列表,imagePaths:商品图,prices:价格列表,deliveryTimes:配送时间列表 isReceiveds:是否收货列表(false:未收货 true:已收货) evaluations:评分列表"),
    })
    @PostMapping(value = "/queryOrderBySellerId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerId", value = "卖家id", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "页码大小", required = true, dataType = "int",paramType="query"),

    })
    public String queryOrderBySellerId(@RequestParam int sellerId, @RequestParam int pageNum, @RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<String> imagePaths = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<String> deliveryTimes = new ArrayList<>();
        List<Boolean> isReceiveds = new ArrayList<>();
        List<Integer> evaluations = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PageHelper.startPage(pageNum,pageSize);
        for (Order order : orderService.queryOrderBySellerId(sellerId)) {
            orderIds.add(order.getId());
            goodsIds.add(order.getGoodsId());
            Goods goods = goodsService.queryById(order.getGoodsId());
            goodsInfos.add(goods.getGoodsInfo());
            imagePaths.add(URLFileMap.getGoodsImgPath(goods.getId()));
            prices.add(goods.getPrice());
            deliveryTimes.add(sdf.format(order.getDeliveryTime()));
            isReceiveds.add(order.isReceived());
            evaluations.add(order.getEvaluation());
        }
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"查询出售订单成功!!");
        returnValue.put("orderIds",orderIds);
        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("deliveryTimes",deliveryTimes);
        returnValue.put("prices",prices);
        returnValue.put("isReceiveds",isReceiveds);
        returnValue.put("evaluations",evaluations);
        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回该用户在售商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/allGoodsCount",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "待出售商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String allGoodsCount(int sellerId,@RequestParam int pageSize){
        User user = userService.queryUserById(sellerId);
        JSONObject returnValue = new JSONObject();
        if (pageSize!=0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"该用户所有商品的页码数成功!!");
            logger.info("该用户所有商品的页码数成功");
            returnValue.put("allPagesCount",(goodsService.cntBySellerId(user.getId())+pageSize-1)/pageSize);
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }
        return returnValue.toString();
    }

    @ApiOperation("获得该用户售卖商品信息")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/queryAllGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "商品页的大小", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "sellerId", value = "卖家id", required = true, dataType = "int",paramType="query")
    })
    public String queryAllGoods(int sellerId, @RequestParam int pageNum, @RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = userService.queryUserById(sellerId);
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<List<String>> imagePaths = new ArrayList<>();
        List<List<Integer>> tagIds = new ArrayList<>();
        List<List<String>> tagNames = new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);

        for (Goods goods : goodsService.queryOnSaleGoodsBySellerId(user.getId())) {
            goodsIds.add(goods.getId());
            goodsInfos.add(goods.getGoodsInfo());
            prices.add(goods.getPrice());
            sellerIds.add(goods.getSellerId());
            sellerNames.add(userService.queryUserById(goods.getSellerId()).getUsername());
            List<Integer> tagIdList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            for (Tag_Goods tag_goods : tag_goodsService.queryByGoodsId(goods.getId())) {
                tagIdList.add(tag_goods.getTagId());
                tagNameList.add(tagService.queryTagNameById(tag_goods.getTagId()));
            }
            tagIds.add(tagIdList);
            tagNames.add(tagNameList);
            imagePaths.add(URLFileMap.getGoodsImgPaths(goods.getId()));
        }

        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("prices",prices);
        returnValue.put("sellerIds",sellerIds);
        returnValue.put("sellerNames",sellerNames);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("tagIds",tagIds);
        returnValue.put("tagNames",tagNames);

        return returnValue.toString();
    }

}
