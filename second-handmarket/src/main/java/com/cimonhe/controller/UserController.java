package com.cimonhe.controller;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.Tag;
import com.cimonhe.pojo.*;
import com.cimonhe.service.*;
import com.cimonhe.utils.*;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Transactional
public class UserController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    Tag_GoodsService tag_goodsService;

    @Autowired
    AddressInfoService addressInfoService;

    @Autowired
    OrderService orderService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MsgService msgService;

    @Autowired
    CollectService collectService;

    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    private final Logger logger = LoggerFactory.getLogger(getClass());//提供日志类.

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${auth.privateKey}")
    private String privateKey ;

    @ApiOperation("退出登录")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/logout",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
    })
    public String logout(HttpServletRequest request){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        redisTemplate.delete("tokenVersion"+user.getId());
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"退出登录成功!!");
        return returnValue.toString();
    }

    @ApiOperation("注销账号")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/cancelUser",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "encryptPwd", value = "加密过的密码", required = true, dataType = "String",paramType="query")
    })
    public String cancelUser(HttpServletRequest request,@RequestParam String encryptPwd) throws Exception {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        String pwd = RSAEncrypt.decrypt(encryptPwd,privateKey);
        if (!passwordEncoder.matches(pwd,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"密码输入错误");
        }
        else
        {
            userService.deleteUserById(user.getId());
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"账号注销成功");
        }
        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回该用户所有商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/allGoodsCount",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "待出售商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String allGoodsCount(HttpServletRequest request,@RequestParam int pageSize){
        User user = jwtUtil.getUserByReq(request);
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

    @ApiOperation("获得该用户所有商品信息")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表 pendingStatusList:审核状态列表  isOnSales:是否在售列表"),
    })
    @PostMapping(value = "/queryAllGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "商品页的大小", required = true, dataType = "int",paramType="query")
    })
    public String queryAllGoods(HttpServletRequest request,@RequestParam int pageNum,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<List<String>> imagePaths = new ArrayList<>();
        List<String> sellerPhones = new ArrayList<>();
        List<List<Integer>> tagIds = new ArrayList<>();
        List<List<String>> tagNames = new ArrayList<>();
        List<Integer> pendingStatusList = new ArrayList<>();
        List<Boolean>isOnSales = new ArrayList<>();

        PageHelper.startPage(pageNum,pageSize);

        for (Goods goods : goodsService.queryBySellerId(user.getId())) {
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
            pendingStatusList.add(goods.getPendingStatus());
            isOnSales.add(goods.isOnSale());
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
        returnValue.put("pendingStatusList",pendingStatusList);
        returnValue.put("isOnSales",isOnSales);

        return returnValue.toString();
    }

    @ApiOperation("删除该用户在售商品:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/deleteOnSaleGoods",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query")

    })
    public String deleteOnSaleGoods(HttpServletRequest request,int goodsId){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        if (goodsService.deleteIsOnSaleGoodsBySellerId(user.getId(),goodsId)==0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"删除该在售商品失败 请检查该用户是否有该在售商品!!");
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"删除该在售商品成功!!");
        }
        return returnValue.toString();
    }

    @ApiOperation("添加订单:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/addOrder",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "deliveryTime", value = "送货时间 格式如：2021-04-09 11:03:02", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true, dataType = "int",paramType="query"),

    })
    public String addOrder(HttpServletRequest request,@RequestParam int goodsId,@RequestParam  String deliveryTime,@RequestParam int addressId){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        AddressInfo addressInfo = addressInfoService.queryAddressInfById(addressId);
        Goods goods = goodsService.queryById(goodsId);
        if (addressInfo==null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"传入的地址id不存在!!");
        }
        else if (goods == null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"传入的商品id不存在!!");
        }
        else if (addressInfo.getUserId()!=user.getId())
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"token携带的用户没有操作这个地址的权限!!");
        }
        else if (goods.getSellerId() == user.getId())
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"不能购买自己出售的商品!!");
        }
        else
        {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = formatter.parse(deliveryTime);//Date格式
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Order order = new Order(goodsId,date,addressId);

            orderService.addOrder(order);
            goodsService.updateIOSbyId(order.getGoodsId());
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"添加订单成功!!");
        }
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
    public String cntOrderBySellerId(HttpServletRequest request,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        int sellerId = user.getId();
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
            @ApiResponse(code=200,message="orderIds:订单列表 goodsIds:商品id列表,goodsInfos:商品信息列表,imagePaths:商品图,prices:价格列表,deliveryTimes:配送时间列表,addressIds: 地址列表 isReceiveds:是否收货列表(false:未收货 true:已收货) buyerIds: 买家id列表 buyerNames:买家名字列表 evaluations:评分列表"),
    })
    @PostMapping(value = "/queryOrderBySellerId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "页码大小", required = true, dataType = "int",paramType="query"),

    })
    public String queryOrderBySellerId(HttpServletRequest request,@RequestParam int pageNum,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        int sellerId = user.getId();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<String> imagePaths = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<String> deliveryTimes = new ArrayList<>();
        List<Integer> addressIds = new ArrayList<>();
        List<Boolean> isReceiveds = new ArrayList<>();
        List<Integer> buyerIds = new ArrayList<>();
        List<String> buyerNames = new ArrayList<>();
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
            addressIds.add(order.getAddressId());
            isReceiveds.add(order.isReceived());
            User buyer = userService.queryUserById(addressInfoService.queryAddressInfById(order.getAddressId()).getUserId());
            buyerIds.add(buyer.getId());
            buyerNames.add(buyer.getUsername());
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
        returnValue.put("addressIds",addressIds);
        returnValue.put("isReceiveds",isReceiveds);
        returnValue.put("buyerIds",buyerIds);
        returnValue.put("buyerNames",buyerNames);
        returnValue.put("evaluations",evaluations);
        return returnValue.toString();
    }

    @ApiOperation("根据页码大小返回根据用户id查询其购买的订单的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/cntOrderByBuyerId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "审核商品页的大小", required = true, dataType = "int",paramType="query"),
//            @ApiImplicitParam(name = "buyerId", value = "买家id", required = true, dataType = "int",paramType="query")
    })
    public String cntOrderByBuyerId(HttpServletRequest request,@RequestParam int pageSize){
        User user = jwtUtil.getUserByReq(request);
        int buyerId = user.getId();
        JSONObject returnValue = new JSONObject();
        if (pageSize==0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"页面大小不应该为0!!");
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"获得购买的订单的页码成功!!");
            returnValue.put("allPagesCount",(orderService.cntOrderByBuyerId(buyerId)+pageSize-1)/pageSize);
        }
        return returnValue.toString();
    }


    @ApiOperation("用户查询其购买的订单:")
    @ApiResponses({
            @ApiResponse(code=200,message="orderIds:订单列表 goodsIds:商品id列表,goodsInfos:商品信息列表,imagePaths:商品图列表,prices:价格列表,deliveryTimes:配送时间列表,addressIds: 地址列表 isReceiveds:是否收货列表(false:未收货 true:已收货) evaluations : 评分列表 sellerIds:卖家列表 sellerNames:卖家名字列表 evaluations:评分列表"),
    })
    @PostMapping(value = "/queryOrderByBuyerId",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "buyerId", value = "买家id", required = true, dataType = "int",paramType="query"),
    })
    public String queryOrderByBuyerId(HttpServletRequest request,@RequestParam int pageNum,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        int buyerId = user.getId();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> goodsIds = new ArrayList<>();
        List<String> goodsInfos = new ArrayList<>();
        List<String> imagePaths = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<String> deliveryTimes = new ArrayList<>();
        List<Integer> addressIds = new ArrayList<>();
        List<Boolean> isReceiveds = new ArrayList<>();
        List<Integer> evaluations = new ArrayList<>();
        List<Integer> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PageHelper.startPage(pageNum,pageSize);
        for (Order order : orderService.queryOrderByBuyerId(buyerId)) {
            orderIds.add(order.getId());
            goodsIds.add(order.getGoodsId());
            Goods goods = goodsService.queryById(order.getGoodsId());
            goodsInfos.add(goods.getGoodsInfo());
            imagePaths.add(URLFileMap.getGoodsImgPath(goods.getId()));
            prices.add(goods.getPrice());
            deliveryTimes.add(sdf.format(order.getDeliveryTime()));
            addressIds.add(order.getAddressId());
            isReceiveds.add(order.isReceived());
            evaluations.add(order.getEvaluation());
            User seller = userService.queryUserById(goods.getSellerId());
            sellerIds.add(seller.getId());
            sellerNames.add(seller.getUsername());
            evaluations.add(order.getEvaluation());
        }
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"查询购买订单成功!!");
        returnValue.put("orderIds",orderIds);
        returnValue.put("goodsIds",goodsIds);
        returnValue.put("goodsInfos",goodsInfos);
        returnValue.put("imagePaths",imagePaths);
        returnValue.put("prices",prices);
        returnValue.put("deliveryTimes",deliveryTimes);
        returnValue.put("addressIds",addressIds);
        returnValue.put("isReceiveds",isReceiveds);
        returnValue.put("evaluations",evaluations);
        returnValue.put("sellerIds",sellerIds);
        returnValue.put("sellerNames",sellerNames);
        return returnValue.toString();
    }


    @ApiOperation("更新购买订单为已收货并且进行评分:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upOrderIsReceived",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "evaluation", value = "评分", required = true, dataType = "int",paramType="query"),
    })
    public String upOrderIsReceived(HttpServletRequest request,@RequestParam int orderId,@RequestParam int evaluation) {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        if(orderService.upOrderIsReceivedByIdAndBuyerId(orderId,evaluation,user.getId())==0)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"更新失败，该用户没有购买该订单!");
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"确认收货成功!");
        }
        return returnValue.toString();
    }


    @ApiOperation("上传商品:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upGoods",produces = { "application/json;charset=UTF-8" }, headers = "content-type=multipart/form-data")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsInfo", value = "商品信息", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "price", value = "价格（double类型）", required = true, dataType = "double",paramType="query"),
            @ApiImplicitParam(name = "sellerPhone", value = "卖家电话", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name="tagsIds", value="标签列表", required=true, paramType="query" ,allowMultiple=true, dataType = "int"),
    })
    public String upGoods(HttpServletRequest request,@RequestParam String goodsInfo,@RequestParam double price,@RequestParam String sellerPhone, @RequestParam int[] tagsIds ,@RequestParam  MultipartFile[] files){
        JSONObject returnValue = new JSONObject();
        if (!ValidateUtil.checkFiles(files))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"传入文件格式错误 支持的格式为jpg,gif,png,ico,bmp,jpeg!");
        }
        else
        {
            User user = jwtUtil.getUserByReq(request);
            int sellerId = user.getId();
            System.out.println("goodsInfo:"+goodsInfo);
            System.out.println("price"+price);
            System.out.println("sellerId"+sellerId);
            System.out.println("sellerPhone"+sellerPhone);
            Goods goods = new Goods(goodsInfo,price,sellerId,sellerPhone);
            goodsService.addGoods(goods);
            for (int tagsId : tagsIds) {
                System.out.println("tagsIds"+tagsId);
                tag_goodsService.addTag_Goods(new Tag_Goods(tagsId,goods.getId()));
            }
            String path = "C:\\second_hand_market"+"/goods/"+goods.getId()+"/";
            File realPath = new File(path);
            if (!realPath.exists())
                realPath.mkdirs();
            int no=0;
            for (MultipartFile file: files)
            {
                no++;
                System.out.println("$$$$");
                String fileName = file.getOriginalFilename();
                try {
                    System.out.println(path+no+fileName.substring(fileName.lastIndexOf(".")));
                    file.transferTo(new File(path+no+fileName.substring(fileName.lastIndexOf("."))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"上传商品成功!");
        }
        return returnValue.toString();
    }

    @ApiOperation("添加地址:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/addAddress",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consigneeName", value = "收货人姓名", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "address", value = "收货地址", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "houseNo", value = "门牌号", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "phone", value = "联系电话", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "isDefaultAddress", value = "是否为默认地址: (Boolean)", required = true, dataType = "Boolean",paramType="query"),
    })
    public String addAddress(HttpServletRequest request,@RequestParam String consigneeName, @RequestParam String address, @RequestParam int houseNo,@RequestParam String phone,@RequestParam boolean isDefaultAddress){
        JSONObject returnValue = new JSONObject();
        if (!ValidateUtil.validateMobile(phone))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"请输入正确格式的手机号");
        }
        else
        {
            User user = jwtUtil.getUserByReq(request);
            int userId = user.getId();
            AddressInfo addressInfo = new AddressInfo(userId,consigneeName,address,houseNo,phone,isDefaultAddress);
            System.out.println((!isDefaultAddress));
            System.out.println(addressInfoService.queryAllAddressInfoByUserId(userId).size()==0);
            if ((!isDefaultAddress)&&(addressInfoService.queryAllAddressInfoByUserId(userId).size()!=0))
            {
                addressInfoService.addAddressInfo(addressInfo);
            }
            else
            {
                addressInfoService.cancelDefault();
                addressInfo.setDefaultAddress(true);
                addressInfoService.addAddressInfo(addressInfo);
            }

            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"添加地址成功");
        }
        return returnValue.toString();
    }

    @ApiOperation("设置新的默认地址:")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/updateDefaultAddress",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true, dataType = "int",paramType="query"),
    })
    public String updateDefaultAddress(HttpServletRequest request,@RequestParam int addressId){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        AddressInfo addressInfo = addressInfoService.queryAddressInfById(addressId);
        if (addressInfo == null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"给的地址id不存在!!");
        }
        else if (addressInfo.getUserId()!=user.getId())
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该token携带的用户没有该地址id的权限");
        }
        else{
            addressInfoService.cancelDefault();
            addressInfoService.setDefault(addressId);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新默认地址成功");
        }
        return returnValue.toString();
    }

    @ApiOperation("返回所有地址:")
    @ApiResponses({
            @ApiResponse(code=200,message="queryDefaultAddressInfo( ( 多个) id:地址id consigneeName:收货人姓名 address:收货地址 houseNo:门牌号 email:联系方式 isDefaultAddress:是否为默认地址: (Boolean)_"),
    })
    @PostMapping(value = "/allAddress",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
    })
    public String allAddress(HttpServletRequest request){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        int userId = user.getId();
        List<AddressInfo> addressInfos = addressInfoService.queryAllAddressInfoByUserId(userId);
        returnValue.put("addressInfos",addressInfos);
        return returnValue.toString();
    }

    @ApiOperation("根据地址id查询地址")
    @ApiResponses({
            @ApiResponse(code=200,message="(addressInfo : (id:地址id consigneeName:收货人姓名 address:收货地址 houseNo:门牌号 email:联系方式 isDefaultAddress:是否为默认地址: (Boolean))"),
    })
    @PostMapping(value = "/queryAddressInfById",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true, dataType = "int",paramType="query"),
    })
    public String queryAddressInfById(HttpServletRequest request,@RequestParam int addressId){
        User user = jwtUtil.getUserByReq(request);
        JSONObject returnValue = new JSONObject();
        if (addressInfoService.queryAddressInfById(addressId).getUserId()!=user.getId())
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该token携带的用户没有该地址id的权限");
        }
        else
        {
            AddressInfo addressInfo = addressInfoService.queryAddressInfById(addressId);
            returnValue.put("addressInfo",addressInfo);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"根据地址id查询地址成功");

        }
        return returnValue.toString();
    }

    @ApiOperation("删除地址")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/deleteAddress",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true, dataType = "int",paramType="query"),
    })
    public String deleteAddress(HttpServletRequest request,@RequestParam int addressId){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        AddressInfo addressInfo = addressInfoService.queryAddressInfById(addressId);
        if (addressInfo == null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"给的地址id不存在!!");
        }

        else if (addressInfo.getUserId()!=user.getId())
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该token携带的用户没有该地址id的权限");
        }
        else
        {
            if (addressInfo.isDefaultAddress())
            {
                returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
                returnValue.put(ReturnValueConstant.MSG,"不能删除默认地址应当修改默认地址后再删除");
            }
            else
            {
                addressInfoService.deleteAddressInfo(addressId);
                returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
                returnValue.put(ReturnValueConstant.MSG,"删除地址成功");
            }
        }
        return returnValue.toString();
    }

    @ApiOperation("添加留言")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/addMsg",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "content", value = "留言内容", required = true, dataType = "int",paramType="query"),
    })
    public String addMsg(HttpServletRequest request,@RequestParam int goodsId,@RequestParam String content) throws Exception {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        String detectionResult = ContextSafe.detection(content);
        if (detectionResult=="normal")
        {
            msgService.addMsg(new Msg(goodsId,content, new Date(),user.getId()));
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"添加留言成功");
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"由于留言违规,添加留言失败 违规类型:"+detectionResult);
        }
        return returnValue.toString();
    }

    @ApiOperation("点击收藏按钮")
    @ApiResponses({
            @ApiResponse(code=200,message="hasCollected:是否被收藏 true为被收藏 false为没被收藏"),
    })
    @PostMapping(value = "/pressCollect",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
    })
    public String pressCollect(HttpServletRequest request,@RequestParam int goodsId){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        Collect collect = new Collect(user.getId(),goodsId);
        if (collectService.queryCollectByCollect(collect)==null)
        {
            collectService.addCollect(collect);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"添加收藏成功");
            returnValue.put("hasCollected",true);

        }
        else
        {
            collectService.deleteCollect(collect);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"删除收藏成功");
            returnValue.put("hasCollected",false);

        }
        return returnValue.toString();
    }

    @ApiOperation("根据页码大小该用户收藏商品的页码数")
    @ApiResponses({
            @ApiResponse(code=200,message="allPagesCount: 页码数"),
    })
    @PostMapping(value = "/cntCollectByUser",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "搜索待出售商品页的大小", required = true, dataType = "int",paramType="query"),
    })
    public String cntCollectByUser(HttpServletRequest request,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        returnValue.put("allPagesCount",(collectService.cntGoodsByCollectorId(user.getId())+pageSize-1)/pageSize);
        return returnValue.toString();
    }

    @ApiOperation("返回该用户收藏的商品")
    @ApiResponses({
            @ApiResponse(code=200,message="goodsIds:商品id列表 goodsInfos:商品信息列表 prices:价格列表 sellerIds:卖家id列表 sellerNames:卖家名字列表 imagePaths:商品图[二维]列表 sellerPhones:卖家联系方式列表 tagIds:标签id[二维]列表 tagNames:标签名[二维]列表"),
    })
    @PostMapping(value = "/collectOfUser",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "搜索商品页页码", required = true, dataType = "int",paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "搜索商品页的大小", required = true, dataType = "int",paramType="query"),
    })
    public String collectOfUser(HttpServletRequest request,@RequestParam int pageNum,@RequestParam int pageSize){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
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
        for (Goods goods : collectService.queryGoodsByCollectorId(user.getId())) {
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

    @ApiOperation("用户上传所需的标签")
    @ApiResponses({
    })
    @PostMapping(value = "/upTag",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagName", value = "标签名", required = true, dataType = "String",paramType="query"),
    })
    public String upTag(HttpServletRequest request,@RequestParam String tagName)
    {
        JSONObject returnValue = new JSONObject();
        tagService.upTag(new Tag(tagName));
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"上传标签成功");
        return returnValue.toString();
    }

    @ApiOperation("返回用户是否收藏该商品")
    @ApiResponses({
            @ApiResponse(code=200,message="isCollected:是否已经收藏"),
    })
    @PostMapping(value = "/isCollected",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "int",paramType="query"),
    })
    public String isCollected(HttpServletRequest request,@RequestParam int goodsId)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        Collect collect = collectService.queryCollectByCollect(new Collect(user.getId(),goodsId));
        if (collect==null)
        {
            returnValue.put("isCollected",false);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"还未收藏");
        }
        else
        {
            returnValue.put("isCollected",true);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"已经收藏");
        }
        return returnValue.toString();
    }

    @ApiOperation("用户获得自身的个人主页信息")
    @ApiResponses({
            @ApiResponse(code=200,message="user (id:用户信息 email:邮箱 unqualifiedNum:不合格商品数 isAdmin:是否是管理员 gender:性别(false:女性 true:男性) grade:年级 sno:学号 academy:学院 major:专业 introduction:个人简介) portraitPath:头像存储路径"),
    })
    @PostMapping(value = "/userProfile",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
    })
    public String userProfile(HttpServletRequest request)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        user.setPassword("密码不可见");
        returnValue.put("user",user);
        returnValue.put("portraitPath",URLFileMap.getPortraitImgPath(user.getId()));
        return returnValue.toString();
    }

    @ApiOperation("获得其他用户的个人主页信息")
    @ApiResponses({
            @ApiResponse(code=200,message="username:用户名 introduction:个人简介) portraitPath:头像存储路径"),
    })
    @PostMapping(value = "/otherUserProfile",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "int",paramType="query"),
    })
    public String otherUserProfile(@RequestParam int userId)
    {
        User user = userService.queryUserById(userId);
        JSONObject returnValue = new JSONObject();
        user.setPassword("密码不可见");
        returnValue.put("user",user);
        returnValue.put("portraitPath",URLFileMap.getPortraitImgPath(user.getId()));
        return returnValue.toString();
    }

    @ApiOperation("通过验证旧密码更新用户密码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upPwdByOldPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldEncryptPwd", value = "旧加密密码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "newEncryptPwd", value = "新加密新密码", required = true, dataType = "String",paramType="query"),
    })
    public String upPwdByOldPwd(HttpServletRequest request,@RequestParam String oldEncryptPwd,@RequestParam String newEncryptPwd) throws Exception {
        User user = jwtUtil.getUserByReq(request);
        JSONObject returnValue = new JSONObject();
        String oldPwd = RSAEncrypt.decrypt(oldEncryptPwd,privateKey);
        String newPwd = RSAEncrypt.decrypt(newEncryptPwd,privateKey);
        if (oldPwd.equals(newPwd))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"旧密码和新密码不能相同");
        }
        else if (!passwordEncoder.matches(oldPwd,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"旧密码输入错误");
        }
        else
        {
            userService.upPasswordById(user.getId(),passwordEncoder.encode(newPwd));
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新密码成功");
            redisTemplate.delete("tokenVersion"+user.getId());
        }
        return returnValue.toString();
    }

    @ApiOperation("通过验证邮箱更新用户密码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upPwdByEmail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newEncryptPwd", value = "新加密密码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String",paramType="query"),
    })
    public String upPwdByEmail(HttpServletRequest request,@RequestParam String newEncryptPwd,@RequestParam String verifyCode) throws Exception {
        JSONObject returnValue = new JSONObject();
        String newPwd = RSAEncrypt.decrypt(newEncryptPwd,privateKey);
        User user = jwtUtil.getUserByReq(request);
        String upPwdVerifyCode =(String) request.getSession().getAttribute("upPwdVerifyCode");
        if (passwordEncoder.matches(newPwd,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"新密码不应该与原密码相同");
        }
        else if (!upPwdVerifyCode.equals(verifyCode))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"验证码输入错误");
        }
        else
        {
            userService.upPasswordById(user.getId(),passwordEncoder.encode(newPwd));
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"修改密码成功");
            request.getSession().removeAttribute("upPwdVerifyCode");
            redisTemplate.delete("tokenVersion"+user.getId());
        }
        return returnValue.toString();
    }


    @ApiOperation("发送修改密码邮箱验证码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/sendUpPwdMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({

    })
    public String sendUpPwdMail(HttpServletRequest request) {
        JSONObject returnValue = new JSONObject();
        User user= jwtUtil.getUserByReq(request);
        String email = user.getEmail();
        MailVo mailVo = new MailVo();
        String upPwdVerifyCode = String.valueOf(((int)((Math.random() * 9 + 1) * 100000)));

        mailVo.setSubject("易物二手市场修改密码邮箱验证码");
        mailVo.setText("验证码为"+upPwdVerifyCode);
        mailVo.setTo(email);

        request.getSession().removeAttribute("upPwdVerifyCode");
        request.getSession().setAttribute("upPwdVerifyCode",upPwdVerifyCode);
        mailService.sendMail(mailVo);//发送邮件和附件
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"发送验证码成功");
        return returnValue.toString();
    }

    @ApiOperation("通过验证旧密码更新用户邮箱")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upEmailByOldPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldEncryptPwd", value = "旧加密密码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "newEmail", value = "邮箱", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "newEmailVerifyCode", value = "新邮箱验证码", required = true, dataType = "String",paramType="query"),
    })
    public String upEmailByOldPwd(HttpServletRequest request,@RequestParam String oldEncryptPwd,@RequestParam String newEmail,@RequestParam String newEmailVerifyCode) throws Exception {

        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        String oldPwd = RSAEncrypt.decrypt(oldEncryptPwd,privateKey);
        if (!ValidateUtil.validateEmail(newEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"请输入正确格式的邮箱");
            return returnValue.toString();
        }
        else if (user.getEmail().equals(newEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"修改的邮箱不能与原来邮箱一致");
        }
        else if(userService.queryUserByEmail(newEmail)!=null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该邮箱已被注册，请更换邮箱");
        }
        else if (!newEmail.equals(request.getSession().getAttribute("newEmail")))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"此邮箱非发送验证码的新邮箱");
        }
        else if (!newEmailVerifyCode.equals(request.getSession().getAttribute("upNewEmailVerifyCode")))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put("msg","新邮箱的验证码输入错误");
        }
        else if (!passwordEncoder.matches(oldPwd,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"旧密码输入错误");
        }
        else
        {
            userService.upEmailById(user.getId(),newEmail);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新邮箱成功");
            request.getSession().removeAttribute("upNewEmailVerifyCode");
            request.getSession().removeAttribute("newEmail");
        }
        return returnValue.toString();
    }

    @ApiOperation("通过验证邮箱更新用户邮箱")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upEmailByEmail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newEmail", value = "新邮箱", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "oldEmailVerifyCode", value = "旧邮箱验证码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "newEmailVerifyCode", value = "新邮箱验证码", required = true, dataType = "String",paramType="query"),
    })
    public String upEmailByEmail(HttpServletRequest request,@RequestParam String newEmail,@RequestParam String oldEmailVerifyCode,@RequestParam String newEmailVerifyCode){
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        String upOldEmailVerifyCode =(String) request.getSession().getAttribute("upOldEmailVerifyCode");
        if (!ValidateUtil.validateEmail(newEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"请输入正确格式的邮箱");
            return returnValue.toString();
        }
        else if (user.getEmail().equals(newEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"修改的邮箱不能与原来邮箱一致");
        }
        else if(userService.queryUserByEmail(newEmail)!=null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该邮箱已被注册，请更换邮箱");
        }
        else if (!newEmail.equals(request.getSession().getAttribute("newEmail")))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"此邮箱非发送验证码的新邮箱");
        }
        else if (!newEmailVerifyCode.equals(request.getSession().getAttribute("upNewEmailVerifyCode")))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put("msg","新邮箱的验证码输入错误");
        }
        else if (!upOldEmailVerifyCode.equals(oldEmailVerifyCode))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"验证码输入错误");
        }
        else
        {
            userService.upEmailById(user.getId(),newEmail);
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新邮箱成功");
            request.getSession().removeAttribute("upOldEmailVerifyCode");
            request.getSession().removeAttribute("upNewEmailVerifyCode");
            request.getSession().removeAttribute("newEmail");
        }
        return returnValue.toString();
    }

    @ApiOperation("发送更新邮箱 旧邮箱验证码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回"),
    })
    @PostMapping(value = "/sendUpOldEmailMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({

    })
    public String sendUpOldEmailMail(HttpServletRequest request) {
        JSONObject returnValue = new JSONObject();
        User user= jwtUtil.getUserByReq(request);
        String email = user.getEmail();
        MailVo mailVo = new MailVo();
        String upOldEmailVerifyCode = String.valueOf(((int)((Math.random() * 9 + 1) * 100000)));

        mailVo.setSubject("易物二手市场更新邮箱 旧邮箱验证码");
        mailVo.setText("验证码为"+upOldEmailVerifyCode);
        mailVo.setTo(email);

        request.getSession().removeAttribute("upOldEmailVerifyCode");
        request.getSession().setAttribute("upOldEmailVerifyCode",upOldEmailVerifyCode);
        mailService.sendMail(mailVo);//发送邮件和附件
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"发送验证码成功");
        return returnValue.toString();
    }

    @ApiOperation("发送更新邮箱 新邮箱验证码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/sendUpNewEmailMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newEmail", value = "新邮箱", required = true, dataType = "String",paramType="query"),
    })
    public String sendUpNewEmailMail(HttpServletRequest request,@Validated @Email(message = "请输入正确格式的邮箱") String newEmail) {
        JSONObject returnValue = new JSONObject();
         // true
        if (!ValidateUtil.validateEmail(newEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"请输入正确格式的邮箱");
        }
        else
        {
            MailVo mailVo = new MailVo();
            String upNewEmailVerifyCode = String.valueOf((((int)((Math.random() * 9 + 1) * 100000))));

            mailVo.setSubject("易物二手市场更新邮箱 新邮箱验证码");
            mailVo.setText("验证码为"+upNewEmailVerifyCode);
            mailVo.setTo(newEmail);

            request.getSession().removeAttribute("upNewEmailVerifyCode");
            request.getSession().removeAttribute("newEmail");
            request.getSession().setAttribute("upNewEmailVerifyCode",upNewEmailVerifyCode);
            request.getSession().setAttribute("newEmail",newEmail);
            mailService.sendMail(mailVo).toString();//发送邮件和附件
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"发送验证码成功");
        }
        return returnValue.toString();
    }



    @ApiOperation("更新用户名")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upUsername",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newUsername", value = "新用户名", required = true, dataType = "String",paramType="query"),
    })
    public String upUsername(HttpServletRequest request,@RequestParam String newUsername)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        if (userService.queryUserByName(newUsername)!=null)
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该用户名已被注册，请输入其他的新用户名");
        }
        else
        {
            userService.upUsernameById(user.getId(),newUsername);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新用户名成功");
        }
        return returnValue.toString();
    }

    @ApiOperation("更新头像")
    @ApiResponses({
            @ApiResponse(code=200,message="portraitPath: 头像路径"),
    })
    @PostMapping(value = "/upPortrait",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newPortrait", value = "新头像", required = true, dataType = "file",paramType="query"),
    })
    public String upPortrait(HttpServletRequest request,@RequestParam MultipartFile newPortrait) throws IOException {
        JSONObject returnValue = new JSONObject();
        if (newPortrait == null||newPortrait.isEmpty())
        {

            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"上传图片不应该为空");
        }
        else if (!ValidateUtil.checkFile(newPortrait.getOriginalFilename()))
        {
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"上传图片格式错误 支持的格式为jpg,gif,png,ico,bmp,jpeg");
        }
        else
        {
            User user = jwtUtil.getUserByReq(request);
            URLFileMap.upPortrait(user.getId(),newPortrait);
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"更新用户头像成功");
            returnValue.put("portraitPath",URLFileMap.getPortraitImgPath(user.getId()));

        }
        return returnValue.toString();
    }

    @ApiOperation("更新用户性别")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upGender",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gender", value = "用户性别 0(false)代表女性，1(true)代表男性）", required = true, dataType = "boolean",paramType="query"),
    })
    public String upGender(HttpServletRequest request,@RequestParam boolean gender)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upGenderById(user.getId(),gender);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户性别成功");
        return returnValue.toString();
    }

    @ApiOperation("更新用户年级")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upGrade",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grade", value = "年级", required = true, dataType = "String",paramType="query"),
    })
    public String upGrade(HttpServletRequest request,@RequestParam String grade)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upGradeById(user.getId(),grade);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户年级成功");
        return returnValue.toString();
    }

    @ApiOperation("更新用户学号")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upSno",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sno", value = "学号", required = true, dataType = "String",paramType="query"),
    })
    public String upSno(HttpServletRequest request,@RequestParam String sno)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upSnoById(user.getId(),sno);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户学号成功");
        return returnValue.toString();
    }

    @ApiOperation("更新用户学院")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upAcademy",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "academy", value = "学院", required = true, dataType = "String",paramType="query"),
    })
    public String upAcademy(HttpServletRequest request,@RequestParam String academy)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upAcademyById(user.getId(),academy);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户学院成功");
        return returnValue.toString();
    }

    @ApiOperation("更新用户专业")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upMajor",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "major", value = "专业", required = true, dataType = "String",paramType="query"),
    })
    public String upMajor(HttpServletRequest request,@RequestParam String major)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upMajorById(user.getId(),major);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户专业成功");
        return returnValue.toString();
    }

    @ApiOperation("更新简介专业")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/upIntroduction",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "introduction", value = "简介", required = true, dataType = "String",paramType="query"),
    })
    public String upIntroduction(HttpServletRequest request,@RequestParam String introduction)
    {
        JSONObject returnValue = new JSONObject();
        User user = jwtUtil.getUserByReq(request);
        userService.upIntroductionById(user.getId(),introduction);
        returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"更新用户简介成功");
        return returnValue.toString();
    }

}
