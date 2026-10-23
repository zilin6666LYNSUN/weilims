<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item :label="$t('system.用户昵称')" prop="nickName">
      <el-input v-model="user.nickName" maxlength="30" />
    </el-form-item> 
    <el-form-item :label="$t('system.手机号码')" prop="phonenumber">
      <el-input v-model="user.phonenumber" maxlength="11" />
    </el-form-item>
    <el-form-item :label="$t('system.邮箱')" prop="email">
      <el-input v-model="user.email" maxlength="50" />
    </el-form-item>
    <el-form-item :label="$t('system.性别')">
      <el-radio-group v-model="user.sex">
        <el-radio label="0">{{ this.$t('system.男') }}</el-radio>
        <el-radio label="1">{{ this.$t('system.女') }}</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">{{ this.$t('common.save') }}</el-button>
      <el-button type="danger" size="mini" @click="close">{{ this.$t('system.关闭') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserProfile } from "@/api/system/user";

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    return {
      // 表单校验
      rules: {
        nickName: [
          { required: true, message: this.$t('system.用户昵称不能为空'), trigger: "blur" }
        ],
        email: [
          { required: true, message: this.$t('system.邮箱地址不能为空'), trigger: "blur" },
          {
            type: "email",
            message: this.$t('system.请输入正确的邮箱地址'),
            trigger: ["blur", "change"]
          }
        ],
        phonenumber: [
          { required: true, message: this.$t('system.手机号码不能为空'), trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: this.$t('system.请输入正确的手机号码'),
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserProfile(this.user).then(response => {
            this.$modal.msgSuccess(this.$t('system.修改成功'));
          });
        }
      });
    },
    close() {
      this.$tab.closePage();
    }
  }
};
</script>
