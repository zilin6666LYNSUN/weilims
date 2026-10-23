<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item :label="$t('system.旧密码')" prop="oldPassword">
      <el-input v-model="user.oldPassword" :placeholder="$t('system.请输入旧密码')" type="password" show-password/>
    </el-form-item>
    <el-form-item :label="$t('system.新密码')" prop="newPassword">
      <el-input v-model="user.newPassword" :placeholder="$t('system.请输入新密码')" type="password" show-password/>
    </el-form-item>
    <el-form-item :label="$t('system.确认密码')" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" :placeholder="$t('system.请确认密码')" type="password" show-password/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">{{ this.$t('common.save') }}</el-button>
      <el-button type="danger" size="mini" @click="close">{{ this.$t('system.关闭') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPassword !== value) {
        callback(new Error(this.$t('system.两次输入的密码不一致')));
      } else {
        callback();
      }
    };
    return {
      user: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: this.$t('system.旧密码不能为空'), trigger: "blur" }
        ],
        newPassword: [
          { required: true, message: this.$t('system.新密码不能为空'), trigger: "blur" },
          { min: 6, max: 20, message: this.$t('system.长度在 6 到 20 个字符'), trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: this.$t('system.确认密码不能为空'), trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
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
