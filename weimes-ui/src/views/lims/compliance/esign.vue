<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="88px">
      <el-form-item :label="$t('lims.签名对象')" prop="recordType">
        <el-input v-model="queryParams.recordType" :placeholder="$t('lims.签名对象类型')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.签名人')" prop="signerName">
        <el-input v-model="queryParams.signerName" :placeholder="$t('lims.请输入签名人')" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item :label="$t('lims.签名含义')" prop="signatureMeaning">
        <el-select v-model="queryParams.signatureMeaning" :placeholder="$t('lims.请选择')" clearable>
          <el-option :label="$t('lims.起草')" value="DRAFTED" />
          <el-option :label="$t('lims.审核')" value="REVIEWED" />
          <el-option :label="$t('lims.批准')" value="APPROVED" />
          <el-option :label="$t('lims.驳回')" value="REJECTED" />
          <el-option :label="$t('lims.验证')" value="VERIFIED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ this.$t('lims.搜索') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ this.$t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="signatureList" border>
      <el-table-column :label="$t('lims.签名ID')" align="center" prop="signatureId" width="80" />
      <el-table-column :label="$t('lims.签名对象类型')" align="center" prop="recordType" width="140" />
      <el-table-column :label="$t('lims.对象ID')" align="center" prop="recordId" width="80" />
      <el-table-column :label="$t('lims.签名人')" align="center" prop="signerName" width="100" />
      <el-table-column :label="$t('lims.签名含义')" align="center" prop="signatureMeaning" width="80">
        <template slot-scope="scope">
          <el-tag :type="getMeaningTagType(scope.row.signatureMeaning)" size="small">{{ getMeaningLabel(scope.row.signatureMeaning) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.签名时间')" align="center" prop="signatureTime" width="160" />
      <el-table-column :label="$t('lims.签名理由')" align="center" prop="reason" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column :label="$t('lims.是否有效')" align="center" prop="isValid" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isValid === 1 ? 'success' : 'danger'" size="small">{{ scope.row.isValid === 1 ? $t('lims.有效') : $t('lims.无效') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('lims.IP地址')" align="center" prop="ipAddress" width="130" />
      <el-table-column :label="$t('common.operation')" align="center" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleViewSignatures(scope.row)" v-hasPermi="['lims:esign:query']">{{ $t('lims.查看') }}</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleVerify(scope.row)" v-hasPermi="['lims:esign:verify']">{{ $t('lims.验证') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 签名验证对话框 -->
    <el-dialog :title="$t('menu.签名验证')" :visible.sync="verifyOpen" width="400px" append-to-body>
      <el-form ref="verifyForm" :model="verifyForm" label-width="80px">
        <el-form-item :label="$t('lims.签名ID')">
          <el-input v-model="verifyForm.signatureId" disabled />
        </el-form-item>
        <el-form-item :label="$t('login.password')" prop="password">
          <el-input v-model="verifyForm.password" type="password" :placeholder="$t('lims.请输入验证密码')" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="verifyOpen = false">{{ this.$t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitVerify">{{ this.$t('lims.验证') }}</el-button>
      </div>
    </el-dialog>

    <!-- 业务签名记录对话框 -->
    <el-dialog :title="$t('lims.签名记录')" :visible.sync="signaturesOpen" width="70%" append-to-body>
      <el-table v-loading="signaturesLoading" :data="signaturesList" border>
        <el-table-column :label="$t('lims.签名人')" align="center" prop="signerName" width="100" />
        <el-table-column :label="$t('lims.签名含义')" align="center" prop="signatureMeaning" width="80">
          <template slot-scope="scope">
            <el-tag :type="getMeaningTagType(scope.row.signatureMeaning)" size="small">{{ getMeaningLabel(scope.row.signatureMeaning) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('lims.签名时间')" align="center" prop="signatureTime" width="160" />
        <el-table-column :label="$t('lims.签名理由')" align="center" prop="reason" :show-overflow-tooltip="true" min-width="150" />
        <el-table-column :label="$t('lims.是否有效')" align="center" prop="isValid" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isValid === 1 ? 'success' : 'danger'" size="small">{{ scope.row.isValid === 1 ? $t('lims.有效') : $t('lims.无效') }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listSignature, verifySignature, getSignatures } from '@/api/lims/esign'

export default {
  name: 'LimsEsign',
  data() {
    return {
      loading: false,
      signatureList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recordType: undefined,
        signerName: undefined,
        signatureMeaning: undefined
      },
      verifyOpen: false,
      verifyForm: {
        signatureId: undefined,
        password: undefined
      },
      signaturesOpen: false,
      signaturesLoading: false,
      signaturesList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSignature(this.queryParams).then(response => {
        this.signatureList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleVerify(row) {
      this.verifyForm.signatureId = row.signatureId
      this.verifyForm.password = undefined
      this.verifyOpen = true
    },
    submitVerify() {
      verifySignature(this.verifyForm.signatureId, this.verifyForm.password).then(response => {
        this.$modal.msgSuccess(response.data ? this.$t('lims.签名验证通过') : $t('lims.签名验证失败'));
        this.verifyOpen = false
      })
    },
    handleViewSignatures(row) {
      this.signaturesOpen = true
      this.signaturesLoading = true
      getSignatures(row.recordType, row.recordId).then(response => {
        this.signaturesList = response.data
        this.signaturesLoading = false
      })
    },
    getMeaningLabel(meaning) {
      const map = { DRAFTED: this.$t('lims.起草'), REVIEWED: this.$t('lims.审核'), APPROVED: this.$t('lims.批准'), REJECTED: this.$t('lims.驳回'), VERIFIED: this.$t('lims.验证') }
      return map[meaning] || meaning
    },
    getMeaningTagType(meaning) {
      const map = { DRAFTED: 'info', REVIEWED: '', APPROVED: 'success', REJECTED: 'danger', VERIFIED: 'warning' }
      return map[meaning] || ''
    }
  }
}
</script>
