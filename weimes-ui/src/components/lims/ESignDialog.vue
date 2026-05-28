<template>
  <div>
    <!-- 电子签名对话框 -->
    <el-dialog title="电子签名确认" :visible.sync="visible" width="450px" append-to-body :close-on-click-modal="false">
      <el-form ref="esignForm" :model="esignForm" :rules="esignRules" label-width="100px">
        <el-form-item label="签名含义" prop="signatureMeaning">
          <el-select v-model="esignForm.signatureMeaning" placeholder="请选择">
            <el-option label="起草" value="DRAFTED"/>
            <el-option label="审核" value="REVIEWED"/>
            <el-option label="批准" value="APPROVED"/>
            <el-option label="拒绝" value="REJECTED"/>
            <el-option label="验证" value="VERIFIED"/>
          </el-select>
        </el-form-item>
        <el-form-item label="签名理由" prop="reason">
          <el-input v-model="esignForm.reason" type="textarea" :rows="3" placeholder="请输入签名理由(合规必填)"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="password">
          <el-input v-model="esignForm.password" type="password" placeholder="请输入登录密码确认身份" show-password/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSign">确认签名</el-button>
        <el-button @click="handleCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createSignature } from '@/api/lims/esign'

export default {
  name: 'ESignDialog',
  props: {
    recordType: { type: String, required: true },
    recordId: { type: Number, required: true }
  },
  data() {
    return {
      visible: false,
      esignForm: {
        signatureMeaning: '',
        reason: '',
        password: ''
      },
      esignRules: {
        signatureMeaning: [{ required: true, message: '请选择签名含义', trigger: 'change' }],
        reason: [{ required: true, message: '请输入签名理由(合规必填)', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码确认身份', trigger: 'blur' }]
      }
    }
  },
  methods: {
    show() {
      this.esignForm = { signatureMeaning: '', reason: '', password: '' }
      this.visible = true
    },
    handleSign() {
      this.$refs['esignForm'].validate(valid => {
        if (valid) {
          const data = {
            recordType: this.recordType,
            recordId: this.recordId,
            signatureMeaning: this.esignForm.signatureMeaning,
            reason: this.esignForm.reason
          }
          createSignature(data).then(response => {
            this.$modal.msgSuccess('签名成功')
            this.visible = false
            this.$emit('signed', response.data)
          })
        }
      })
    },
    handleCancel() {
      this.visible = false
      this.$emit('cancel')
    }
  }
}
</script>
