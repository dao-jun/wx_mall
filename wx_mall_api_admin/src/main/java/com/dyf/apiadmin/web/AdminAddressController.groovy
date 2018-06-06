package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallAddressDO
import com.dyf.db.service.WxMallAddressService
import com.dyf.db.service.WxMallRegionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/address")
class AdminAddressController {


    @Autowired
    private WxMallAddressService addressService
    @Autowired
    private WxMallRegionService regionService

    private def toVo(WxMallAddressDO address) {
        def addressVo = [:]
        addressVo.put("id", address.id)
        addressVo.put("userId", address.userId)
        addressVo.put("name", address.name)
        addressVo.put("mobile", address.mobile)
        addressVo.put("isDefault", address.defaulted)
        addressVo.put("provinceId", address.provinceId)
        addressVo.put("cityId", address.cityId)
        addressVo.put("areaId", address.areaId)
        addressVo.put("address", address.address)
        String province = regionService.selectById(address.provinceId).name
        String city = regionService.selectById(address.cityId).name
        String area = regionService.selectById(address.areaId).name
        addressVo.put("province", province)
        addressVo.put("city", city)
        addressVo.put("area", area)
        addressVo
    }

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             Integer userId, String name,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        def wrapper = new EntityWrapper<WxMallAddressDO>()
        if (userId != null) {
            wrapper.where("user_id = {0}", userId)
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        wrapper.where("deleted = false")

        def addressList = addressService.selectPage(new Page<WxMallAddressDO>(page, limit), wrapper).getRecords()
        def total = addressService.selectCount(wrapper)

        def addressVoList = new ArrayList<>(addressList.size())
        addressList.each { WxMallAddressDO address ->
            def addressVo = toVo(address)
            addressVoList.add(addressVo)
        }

        def data = [:]
        data.put("total", total)
        data.put("items", addressVoList)

        return HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallAddressDO address) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        addressService.insert(address)

        def addressVo = toVo(address)
        return HttpResponse.success(addressVo)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer addressId) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        WxMallAddressDO address = addressService.selectById(addressId)
        def addressVo = toVo(address)
        return HttpResponse.success(addressVo)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallAddressDO address) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        addressService.updateById(address)
        def addressVo = toVo(address)
        return HttpResponse.success(addressVo)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallAddressDO address) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        addressService.deleteById(address.id)
        return HttpResponse.success()
    }

}
