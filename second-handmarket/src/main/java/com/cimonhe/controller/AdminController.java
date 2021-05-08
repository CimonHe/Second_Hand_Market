package com.cimonhe.controller;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.Goods;
import com.cimonhe.pojo.Tag;
import com.cimonhe.pojo.Tag_Goods;
import com.cimonhe.pojo.User;
import com.cimonhe.service.*;
import com.cimonhe.utils.MailVo;
import com.cimonhe.utils.URLFileMap;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
@Transactional
public class AdminController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    Tag_GoodsService tag_goodsService;

    @Autowired
    UserService userService;

    @Autowired
    TagService tagService;

    @Autowired
    private JavaMailSenderImpl mailSender;//注入邮件工具类

    @ApiOperation("根据页码大小返回审核商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/allPendingGoodsPagesCount",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "审核商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String allPendingGoodsPagesCount(@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        if (pageSize!=0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"获得待审核商品的页码数成功!!");
            returnValue.put("allPagesCount",(goodsService.cntPendingGoods()+pageSize-1)/pageSize);
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }
        return returnValue.toString();
    }

    @ApiOperation("显示所有待审核商品")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/showAllPendingGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "审核商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "审核商品页的大小", required = true, dataType = "int",paramType="query")    })
    public String showAllPendingGoods(@RequestParam int pageNum,@RequestParam int pageSize){
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
        for (Goods goods : goodsService.queryPendingGoods()) {
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
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"获得所有待审核商品成功!!");
        return returnValue.toString();
    }

    @ApiOperation("审核商品审核通过")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/pendingGoodsPass",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "审核商品id", required = true, dataType = "int",paramType="query"),
    })
    public String pendingGoodsPass(@RequestParam int goodsId ){
        JSONObject returnValue = new JSONObject();
        goodsService.pendingGoodsPass(goodsId);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"商品审核通过!!");
        return returnValue.toString();
    }

    @ApiOperation("审核商品审核不通过")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/pendingGoodsNotPass",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "审核商品id", required = true, dataType = "int",paramType="query"),
    })
    public String pendingGoodsNotPass(@RequestParam int goodsId ){
        JSONObject returnValue = new JSONObject();
        Goods goods = goodsService.queryById(goodsId);
        if (goods==null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"传入的商品id不存在");
        }
        else
        {
            User user = userService.queryUserById(goods.getSellerId());
            userService.incUnqualifiedNumById(user.getId());
            goodsService.pendingGoodsNotPass(goodsId);
            MailVo mailVo = new MailVo();
            mailVo.setSubject("易物网: 审核商品不通过通知");
            mailVo.setText("尊敬的用户,由于您上传的商品违反社区守则: 所以不通过，请重新上传,您现在的不合格商品数为: "+user.getUnqualifiedNum()+"  如果不合格商品数过多您的账号将会被强制删除 请减少上传不合格商品的次数");
            mailVo.setTo(user.getEmail());
        }
        return returnValue.toString();
    }

    @ApiOperation("获得所有待审核标签")
    @ApiResponses({
            @ApiResponse(code=200,message="tags:标签(包括 tagId:标签ID tagName:标签名)"),
    })
    @PostMapping(value = "/allPendingTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
    })
    public String allPendingTag(){
        JSONObject returnValue = new JSONObject();
        List<Tag> tags = tagService.queryAllPendingTag();
        returnValue.put("tags",tags);
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"获得所有待审核标签成功!");
        return returnValue.toString();
    }

    @ApiOperation("通过审核标签")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/checkTagPass",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "int",paramType="query"),
    })
    public String checkTagPass(@RequestParam int tagId){
        JSONObject returnValue = new JSONObject();
        tagService.checkTagPass(tagId);
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"通过审核标签成功!");
        return returnValue.toString();
    }

    @ApiOperation("删除审核标签")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/deleteTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "int",paramType="query"),
    })
    public String deleteTag(@RequestParam int tagId){
        JSONObject returnValue = new JSONObject();
        tagService.deleteTagById(tagId);
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"删除标签成功!");
        return returnValue.toString();
    }

    @ApiOperation("查询不合格商品数超过uqn的用户")
    @ApiResponses({
            @ApiResponse(code=200,message="users:{}"),
    })
    @PostMapping(value = "/queryUserMoreUnqualifiedNum",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uqn", value = "不合格商品数", required = true, dataType = "int",paramType="query"),
    })
    public String queryUserMoreUnqualifiedNum(@RequestParam int uqn)
    {
        JSONObject returnValue = new JSONObject();
        List<User> users = userService.queryUserMoreUnqualifiedNum(uqn);
        returnValue.put("users",users);
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"查询不合格商品数超过uqn的用户成功!");
        return returnValue.toString();
    }

    @ApiOperation("删除不合格商品数超过uqn的用户")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/deleteUserMoreUnqualifiedNum",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uqn", value = "不合格商品数", required = true, dataType = "int",paramType="query"),
    })
    public String deleteUserMoreUnqualifiedNum(@RequestParam int uqn)
    {
        JSONObject returnValue = new JSONObject();
        for (User user : userService.queryUserMoreUnqualifiedNum(uqn)) {
            MailVo mailVo = new MailVo();
            mailVo.setSubject("易物网 : 同学 你号没了");
            mailVo.setText("由于您上传的商品不合格数大于等于"+uqn+",您的账号已被注销！");
            mailVo.setTo(user.getEmail());
            Thread thread = new MyMailService(mailVo,mailSender);
            thread.start();
        }
        userService.deleteUserMoreUnqualifiedNum(uqn);
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"删除不合格商品数超过"+uqn+"的用户成功!");
        return returnValue.toString();
    }
}
