package com.dyf.db.service

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallOrderDO
import com.dyf.db.repository.WxMallOrderRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Primary
@Transactional
class WxMallOrderService extends ServiceImpl<WxMallOrderRepository, WxMallOrderDO> {

    String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now())
        String orderSn = now + getRandomNum(6)
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = getRandomNum(6)
        }
        return orderSn;
    }

    private static String getRandomNum(Integer num) {
        String base = "0123456789"
        Random random = new Random()
        StringBuffer sb = new StringBuffer()
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length())
            sb.append(base.charAt(number))
        }
        return sb.toString()
    }

    int countByOrderSn(Integer userId, String orderSn) {
        return this.baseMapper.selectCount(new EntityWrapper<WxMallOrderDO>()
                .where("user_id = {0} and order_sn = {1} and deleted = false", userId, orderSn))
    }
}
