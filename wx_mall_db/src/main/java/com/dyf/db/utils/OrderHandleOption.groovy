package com.dyf.db.utils

class OrderHandleOption {
    private boolean cancel = false      // 取消操作
    private boolean delete = false      // 删除操作
    private boolean pay = false         // 支付操作
    private boolean comment = false    // 评论操作
    private boolean confirm = false    // 确认收货操作
    private boolean refund = false     // 取消订单并退款操作
    private boolean rebuy = false        // 再次购买

    void setCancel(boolean cancel) {
        this.cancel = cancel
    }

    void setDelete(boolean delete) {
        this.delete = delete
    }

    void setPay(boolean pay) {
        this.pay = pay
    }

    void setComment(boolean comment) {
        this.comment = comment
    }

    void setConfirm(boolean confirm) {
        this.confirm = confirm
    }

    void setRefund(boolean refund) {
        this.refund = refund
    }

    void setRebuy(boolean rebuy) {
        this.rebuy = rebuy
    }

    boolean isCancel() {
        return cancel
    }

    boolean isDelete() {
        return delete
    }

    boolean isPay() {
        return pay
    }

    boolean isComment() {
        return comment
    }

    boolean isConfirm() {
        return confirm
    }

    boolean isRefund() {
        return refund
    }

    boolean isRebuy() {
        return rebuy
    }

}
