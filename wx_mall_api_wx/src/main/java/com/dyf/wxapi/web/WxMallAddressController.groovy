package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallAddressDO
import com.dyf.db.domain.WxMallRegionAreaDO
import com.dyf.db.domain.WxMallRegionCityDO
import com.dyf.db.domain.WxMallRegionProvinceDO
import com.dyf.db.service.WxMallAddressService
import com.dyf.db.service.WxMallRegionAreaService
import com.dyf.db.service.WxMallRegionCityService
import com.dyf.db.service.WxMallRegionProvinceService
import com.dyf.db.service.WxMallRegionStreetService
import com.dyf.wxapi.annotation.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/address")
class WxMallAddressController {

    @Autowired
    private WxMallAddressService addressService
    @Autowired
    private WxMallRegionStreetService regionStreetService
    @Autowired
    private WxMallRegionAreaService regionAreaService
    @Autowired
    private WxMallRegionCityService regionCityService
    @Autowired
    private WxMallRegionProvinceService regionProvinceService
    /**
     * 收货地址列表
     * @param userId
     * @return
     */
    @GetMapping("/list")
    def list(@LoginUser Integer userId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        def entityWrapper = new EntityWrapper<WxMallAddressDO>()
        entityWrapper.where("user_id = {0}", userId).and("deleted = {0}", false)
        def addressDOList = addressService.selectList(entityWrapper)
        def addressVOList = new LinkedList<Map<String, Object>>()

        addressDOList.forEach({
            address ->
                def addressVO = new HashMap<String, Object>()
                addressVO.put("id", address.id)
                addressVO.put("name", address.name)
                addressVO.put("mobile", address.mobile)
                addressVO.put("defaulted", address.defaulted)

                def province = regionProvinceService.selectOne(new EntityWrapper<WxMallRegionProvinceDO>().where("code = {0}", address.provinceId)).name
                def city = regionCityService.selectOne(new EntityWrapper<WxMallRegionCityDO>().where("code = {0}", address.cityId)).name
                def area = regionAreaService.selectOne(new EntityWrapper<WxMallRegionAreaDO>().where("code = {0}", address.areaId)).name
                def addr = address.address
                def detailAddress = province + city + area + " " + addr
                addressVO.put("detailedAddress", detailAddress)
                addressVOList.add(addressVO)
        })

        HttpResponse.success(addressVOList)
    }

    /**
     * 收货地址详情
     * @param userId
     * @param id
     */
    @GetMapping("/detail")
    def detail(@LoginUser Integer userId, Integer id) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def address = addressService.selectOne(new EntityWrapper<WxMallAddressDO>().where("id = {0}", id))
        if (address == null) {
            return HttpResponse.badArgumentValue()
        }

        def data = new HashMap()
        data.put("id", address.id)
        data.put("name", address.name)
        data.put("provinceId", address.provinceId)
        data.put("cityId", address.cityId)
        data.put("districtId", address.areaId)
        data.put("mobile", address.mobile)
        data.put("address", address.address)
        data.put("defaulted", address.defaulted)
        def pname = regionProvinceService.selectOne(new EntityWrapper<WxMallRegionProvinceDO>().where("code = {0}", address.provinceId)).name
        data.put("provinceName", pname)
        def cname = regionCityService.selectOne(new EntityWrapper<WxMallRegionCityDO>().where("code = {0}", address.cityId)).name
        data.put("cityName", cname)
        def dname = regionAreaService.selectOne(new EntityWrapper<WxMallRegionAreaDO>().where("code = {0}", address.areaId)).name
        data.put("areaName", dname)

        HttpResponse.success(data)
    }
    /**
     * 添加或者更新收货地址
     * @param userId
     * @param addressDO
     */
    @PostMapping("/save")
    def save(@LoginUser Integer userId, @RequestBody WxMallAddressDO addressDO) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (addressDO == null) {
            return HttpResponse.badArgument()
        }

        if (addressDO.defaulted) {
            def addressUpdate = new WxMallAddressDO()
            addressUpdate.defaulted = false
            addressService.update(addressUpdate, new EntityWrapper<WxMallAddressDO>().where("user_id = {0}", userId))
        }

        if (addressDO.id == null || addressDO.id == 0) {
            addressDO.id = null
            addressDO.userId = userId
            addressService.insert(addressDO)
        } else {
            addressDO.userId = userId
            addressService.update(addressDO, new EntityWrapper<WxMallAddressDO>().where("user_id = {0}", userId))
        }
        HttpResponse.success()
    }
    /**
     * 删除收货地址
     * @param userId
     * @param addressDO
     * @return
     */
    @PostMapping("/delete")
    def delete(@LoginUser Integer userId, @RequestBody WxMallAddressDO addressDO) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (addressDO == null) {
            return HttpResponse.badArgument()
        }

        addressService.delete(new EntityWrapper<WxMallAddressDO>().where("id = {0}", addressDO.id))
        HttpResponse.success()
    }

}
