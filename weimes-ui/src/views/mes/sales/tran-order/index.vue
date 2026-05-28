<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="订单编码" prop="tranOrderCode">
        <el-input
          v-model="queryParams.tranOrderCode"
          placeholder="请输入订单编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="queryParams.warehouseId"
          placeholder="请选择仓库"
          clearable
        >
          <el-option
            v-for="dict in warehouseList"
            :key="dict.warehosueId"
            :label="dict.warehouseName"
            :value="dict.warehouseId"
            @keyup.enter.native="handleQuery"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="送货类别" prop="tranOrderType">
        <el-select
          v-model="queryParams.tranOrderType"
          placeholder="请选择送货类别"
          clearable
        >
          <el-option
            v-for="dict in dict.type.tran_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
        >
          <el-option
            v-for="dict in dict.type.mes_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="客户编码" prop="clientCode">
        <el-input
          v-model="queryParams.clientCode"
          placeholder="请输入客户编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="clientName">
        <el-input
          v-model="queryParams.clientName"
          placeholder="请输入客户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总金额" prop="totalAmount">
        <el-input
          v-model="queryParams.totalAmount"
          placeholder="请输入总金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="币别" prop="currency">
        <el-select
          v-model="queryParams.currency"
          placeholder="请选择币别"
          clearable
        >
          <el-option
            v-for="dict in dict.type.currency_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="结账方式" prop="payUp">
        <el-select
          v-model="queryParams.payUp"
          placeholder="请选择结账方式"
          clearable
        >
          <el-option
            v-for="dict in dict.type.pay_up_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="跟单" prop="followerMan">
        <el-input
          v-model="queryParams.followerMan"
          placeholder="请输入跟单"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务员" prop="busMan">
        <el-input
          v-model="queryParams.busMan"
          placeholder="请输入业务员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建者" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入创建者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          clearable
          v-model="queryParams.createTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择创建时间"
        >
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['sales:tranOrder:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['sales:tranOrder:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['sales:tranOrder:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['sales:tranOrder:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="tranOrdeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单号" align="center" prop="tranOrderCode">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">{{
            scope.row.tranOrderCode
          }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="客户编码" align="center" prop="clientCode" />
      <el-table-column label="客户名称" align="center" prop="clientName" />
      <el-table-column label="业务员" align="center" prop="busMan" />
      <el-table-column label="跟单" align="center" prop="followerMan" />
      <el-table-column label="送货类别" align="center" prop="tranOrderType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tran_type"
            :value="scope.row.tranOrderType"
          />
        </template>
      </el-table-column>
      <el-table-column label="仓库" align="center" prop="warehouseName" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.mes_order_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="总金额"
        align="center"
        prop="totalAmount"
        v-if="$auth.hasPermi('sales:tranOrder:money')"
      />
      <el-table-column label="总重量" align="center" prop="weight" />
      <el-table-column label="总件数" align="center" prop="totalPic" />
      <el-table-column label="数量" align="center" prop="total" />
      <el-table-column label="币别" align="center" prop="currency">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.currency_type"
            :value="scope.row.currency"
          />
        </template>
      </el-table-column>
      <el-table-column label="结账方式" align="center" prop="payUp">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pay_up_type" :value="scope.row.payUp" />
        </template>
      </el-table-column>

      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新者" align="center" prop="updateBy" />
      <el-table-column
        label="更新时间"
        align="center"
        prop="updateTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sales:tranOrder:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sales:tranOrder:remove']"
            >删除</el-button
          >
          <el-button
            icon="el-icon-print"
            size="mini"
            type="text"
            @click.stop="printOut(scope.row, true)"
            >打印</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改销售送货单对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1500px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="5">
            <el-form-item label="单号" prop="tranOrderCode">
              <el-input
                v-model="form.tranOrderCode"
                placeholder="请输入订单编码"
              />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item label-width="80">
              <el-switch
                v-model="autoGenFlag"
                active-color="#13ce66"
                active-text="自动生成"
                @change="handleAutoGenChange(autoGenFlag)"
              >
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="日期" prop="tranDate">
              <el-date-picker
                clearable
                v-model="form.tranDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="送货类别" prop="tranOrderType">
              <el-select
                v-model="form.tranOrderType"
                placeholder="请选择送货类别"
              >
                <el-option
                  v-for="dict in dict.type.tran_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="仓库" prop="warehouseId">
              <el-select
                v-model="form.warehouseId"
                placeholder="请选择仓库"
                clearable
                select
              >
                <el-option
                  v-for="dict in warehouseList"
                  :key="dict.warehosueId"
                  :label="dict.warehouseName"
                  :value="dict.warehouseId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="客户编码" prop="clientCode">
              <el-input
                v-model="form.clientCode"
                placeholder="请选择客户"
                :disabled="
                  !this.tranOrderLineList == undefined ||
                  !this.tranOrderLineList.length <= 0
                "
              >
                <el-button
                  slot="append"
                  @click="handleSelectClient"
                  icon="el-icon-search"
                  :disabled="
                    !this.tranOrderLineList == undefined ||
                    !this.tranOrderLineList.length <= 0
                  "
                ></el-button>
              </el-input>
              <ClientSelect ref="clientSelect" @onSelected="onClientSelected">
              </ClientSelect>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户名称" prop="clientName">
              <el-input
                v-model="form.clientName"
                placeholder="请输入客户名称"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="业务员" prop="busMan">
              <el-input v-model="form.busMan" placeholder="请输入业务员" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="跟单" prop="followerMan">
              <el-input v-model="form.followerMan" placeholder="请输入跟单" />
            </el-form-item>
          </el-col>
          <el-col :span="4"
            ><el-form-item label="结账方式" prop="payUp">
              <el-select v-model="form.payUp" placeholder="请选择结账方式">
                <el-option
                  v-for="dict in dict.type.pay_up_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status" v-show="false">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.mes_order_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="客户id" prop="clientId" v-show="false">
          <el-input v-model="form.clientId" placeholder="请输入客户id" />
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="收货地址" prop="address">
              <el-input v-model="form.address" placeholder="收货地址" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="币别" prop="currency">
              <el-select v-model="form.currency" placeholder="请选择币别">
                <el-option
                  v-for="dict in dict.type.currency_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input
                v-model="form.totalAmount"
                placeholder="请输入总金额"
                disabled
              /> </el-form-item
          ></el-col>
          <el-col :span="4">
            <el-form-item label="总重量" prop="weight">
              <el-input
                v-model="form.weight"
                placeholder="请输入总重量"
                disabled
              /> </el-form-item
          ></el-col>
          <el-col :span="4"
            ><el-form-item label="总件数" prop="totalPic">
              <el-input
                v-model="form.totalPic"
                placeholder="请输入总件数"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-divider content-position="center">销售送货单列信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="handleAddTranOrderLine(form)"
              v-if="
                (optType != 'view') &
                (form.status == 'PREPARE' || form.status == '')
              "
              >添加</el-button
            >
          </el-col>

          <el-col :span="1.5">
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="handleDeleteTranOrdeLine"
              v-hasPermi="['sales:tranOrder:remove']"
              v-if="(optType != 'view') & (form.status == 'PREPARE')"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="handleSelectSalesItem(form)"
              v-if="(optType != 'view') & (form.status == 'PREPARE')"
              >从销售订单添加</el-button
            >
            <SalesItemSelect
              ref="salesItemSelect"
              @onSelected="onSalesItemSelect"
            />
          </el-col>
        </el-row>
        <el-table
          :data="tranOrderLineList"
          :row-class-name="rowTranOrdeLineIndex"
          @selection-change="handleTranOrdeLineSelectionChange"
          @row-click="handleRowClick"
          ref="tranOrdeLine"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column
            label="序号"
            align="center"
            prop="index"
            width="50"
          />
          <el-table-column label="仓库" prop="tranCode" width="150">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.warehouseId"
                placeholder="请选择仓库"
                clearable
              >
                <el-option
                  v-for="dict in warehouseList"
                  :key="dict.warehosueId"
                  :label="dict.warehouseName"
                  :value="dict.warehouseId"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column
            label="销售订单编码"
            prop="salesOrderCode"
            width="150"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.salesOrderCode"
                placeholder="请输入销售订单编码"
              />
            </template>
          </el-table-column>
          <el-table-column label="客户PO" prop="PO" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.po" placeholder="请输入PO" />
            </template>
          </el-table-column>
          <el-table-column label="sku" prop="sku" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sku" placeholder="请输入sku" />
            </template>
          </el-table-column>

          <!-- <el-table-column label="送货类别" prop="tranOrderType" width="150">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.tranOrderType"
                placeholder="请选择送货类别"
              >
                <el-option
                  v-for="dict in dict.type.tran_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="送货日期" prop="tranDate" width="240">
            <template slot-scope="scope">
              <el-date-picker
                clearable
                v-model="scope.row.tranDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择送货日期"
              />
            </template>
          </el-table-column> -->
          <!-- <el-table-column label="送货单号" prop="tranCode" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.tranCode"
                placeholder="请输入送货单号"
              />
            </template>
          </el-table-column> -->

          <el-table-column label="产品编码" prop="productCode" width="200">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.productCode"
                placeholder="请选择产品"
              >
                <el-button
                  slot="append"
                  @click="handleSelectProduct"
                  icon="el-icon-search"
                ></el-button>
              </el-input>
              <ItemSelect ref="itemSelect" @onSelected="onItemSelected">
              </ItemSelect>
            </template>
          </el-table-column>
          <el-table-column label="产品名称" prop="productName" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.productName"
                placeholder="请输入产品名称"
              />
            </template>
          </el-table-column>
          <el-table-column label="材质" prop="quality" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.quality" placeholder="请输入材质" />
            </template>
          </el-table-column>
          <el-table-column label="质量等级" prop="level" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.level" placeholder="请选择质量等级">
                <el-option
                  v-for="dict in dict.type.level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="颜色" prop="colorName" width="150">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.colorName"
                placeholder="请选择颜色"
                filterable
              >
                <el-option
                  v-for="item in colorList"
                  :key="item.colorCode"
                  :label="item.colorName"
                  :value="item.colorCode"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" prop="unitOfMeasure" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.unitOfMeasure">
                <el-option
                  v-for="item in measureOptions"
                  :key="item.measureCode"
                  :label="item.measureName"
                  :value="item.measureCode"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" prop="saleSgqty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.saleSgqty"
                placeholder="请输入数量 "
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column label="备品数" prop="saleThqty" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.saleThqty"
                placeholder="请输入备品数"
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column label="单价" prop="amount" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.amount"
                placeholder="请输入单价"
                :change="changeAmount(scope.row)"
              />
            </template>
          </el-table-column>

          <el-table-column label="重量" prop="weight" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.weight" placeholder="请输入重量" :change="changeWeight(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="含税" prop="tax" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.tax">
                <el-option label="是" value="0"></el-option>
                <el-option label="否" value="1"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="金额" prop="totalAmount" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.totalAmount"
                placeholder="请输入金额"
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column label="附加费" prop="extra" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.extra"
                placeholder="请输入附加费"
                type="number"
              />
            </template>
          </el-table-column>
          <el-table-column label="件数" prop="picNum" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.picNum"
                placeholder="请输入件数"
                type="number"
                :change="changePicNum(scope.row)"
              />
            </template>
          </el-table-column>

          <el-table-column
            label="客户品名"
            prop="clientProductName"
            width="150"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.clientProductName"
                placeholder="请输入客户品名"
              />
            </template>
          </el-table-column>
          <el-table-column label="客户颜色" prop="clientColor" width="150">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.clientColor"
                placeholder="请输入客户颜色"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="submitForm"
          v-if="form.status == 'PREPARE' && optType != 'view'"
          >保存</el-button
        >
        <el-button
          type="success"
          @click="handleConfirm('CONFIRMED')"
          v-if="
            form.status == 'PREPARE' &&
            optType != 'view' &&
            form.tranOrderId != null
          "
          >确认</el-button
        >
        <el-button @click="cancel">取 消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          v-if="
            form.status == 'CONFIRMED' &&
            optType != 'view' &&
            form.tranOrderId != null
          "
          >提交审批</el-button
        >
        <el-button
          type="success"
          @click="handleExecute"
          v-if="form.status == 'APPROVING' && form.tranOrderId != null"
          >审批通过</el-button
        >
        <el-button
          type="danger"
          @click="handleReject"
          v-if="form.status == 'APPROVING' && form.tranOrderId != null"
          >审批不通过</el-button
        >
        <el-button
          type="primary"
          @click="handleConfirm('APPROVING')"
          v-if="
            form.status == 'REFUSE' &&
            optType != 'view' &&
            form.tranOrderId != null
          "
          >重新提交</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="modalObj.show"
      :title="modalObj.title"
      :width="modalObj.width"
    >
      <template v-if="modalObj.component === 'print-type'">
        <el-radio-group v-model="modalObj.form.value">
          <el-radio :label="1">lodop打印</el-radio>
          <el-radio :label="2">浏览器打印</el-radio>
        </el-radio-group>
      </template>
      <template
        v-if="
          modalObj.form.value === 2 ||
          modalObj.component === 'window-print-preview'
        "
      >
        <tran-print
          :row="modalObj.form.row"
          ref="receiptOrderPrintRef"
        ></tran-print>
      </template>
      <template slot="footer">
        <el-button @click="modalObj.cancel">取消</el-button>
        <el-button @click="modalObj.ok" type="primary">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  listTranOrde,
  getTranOrde,
  delTranOrde,
  addTranOrde,
  updateTranOrde,
  refuse,
  execute,
} from "@/api/mes/sales/tran/tran-orde";

import ItemSelect from "@/components/itemSelect/item.vue";
import { listWarehouse } from "@/api/mes/wm/warehouse";
import { genCode } from "@/api/system/autocode/rule";
import ClientSelect from "@/components/clientSelect/single.vue";
import SalesItemSelect from "@/components/salesOrder/single.vue";
import { mapGetters } from "vuex";
import { totalListColor } from "@/api/color/color";
import { STOCK_IN_TEMPLATE } from "@/utils/printData";
import tranPrint from "./tranPrint";

export default {
  name: "TranOrder",
  dicts: [
    "currency_type",
    "mes_order_status",
    "pay_up_type",
    "level",
    "tran_type",
  ],
  components: {
    ItemSelect,
    SalesItemSelect,
    ClientSelect,
    tranPrint,
  },
  computed: {
    ...mapGetters({
      measureOptions: "unitmeasureList",
    }),
  },
  data() {
    return {
      optType: undefined,
      //自动生成编码
      autoGenFlag: false,
      modalObj: {
        title: "选择打印方式",
        width: "520px",
        component: null,
        form: {
          value: null,
          row: null,
        },
        ok: () => {},
        cancel: () => {},
      },
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      codes: [],
      //产品列id
      itemIds: [],
      // 子表选中数据
      checkedTranOrdeLine: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 销售送货单表格数据
      tranOrdeList: [],
      // 销售送货单列表格数据
      tranOrderLineList: [],
      //仓库数据
      warehouseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 颜色数据
      colorList: [],
      // 当前选中行
      currentRow: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tranOrderCode: null,
        warehouseId: null,
        tranOrderType: null,
        status: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        totalAmount: null,
        weight: null,
        total: null,
        currency: null,
        payUp: null,
        followerMan: null,
        busMan: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        tranOrderCode: [
          { required: true, message: "订单编码不能为空", trigger: "blur" },
        ],
        tranOrderType: [
          { required: true, message: "送货类别不能为空", trigger: "change" },
        ],
        clientCode: [
          { required: true, message: "客户编码不能为空", trigger: "blur" },
        ],
        clientName: [
          { required: true, message: "客户名称不能为空", trigger: "blur" },
        ],
        currency: [
          { required: true, message: "币别不能为空", trigger: "change" },
        ],
        payUp: [
          { required: true, message: "结账方式不能为空", trigger: "change" },
        ],
        followerMan: [
          { required: true, message: "跟单不能为空", trigger: "blur" },
        ],
        busMan: [
          { required: true, message: "业务员不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getWarehouseList();
    // this.handleRouteChange();
  },
  watch: {
    $route(to, from) {
      this.handleRouteChange();
    },
  },
  methods: {
    printOut(row, print) {
      this.modalObj = {
        show: true,
        title: "选择打印方式",
        width: "520px",
        component: "print-type",
        form: {
          value: 1,
          row,
        },
        ok: () => {
          this.modalObj.show = false;
          if (this.modalObj.form.value === 1) {
            this.doPrintOut(row, false);
          } else {
            this.windowPrintOut(row, print);
          }
        },
        cancel: () => {
          this.modalObj.show = false;
        },
      };
    },
    doPrintOut(row, print) {
      this.getOrderDetail(row).then((result) => {
        if (print) {
          this.$lodop.print(STOCK_IN_TEMPLATE, [result]);
        } else {
          this.$lodop.preview(STOCK_IN_TEMPLATE, [result]);
        }
      });
    },
    windowPrintOut(row, print) {
      this.getOrderDetail(row).then((res) => {
        if (print) {
          this.modalObj.form.row = res.response.data;
          this.$nextTick(() => {
            this.$refs["receiptOrderPrintRef"].start();
          });
          return;
        }
        this.$nextTick(() => {
          this.modalObj = {
            show: true,
            title: "浏览器打印预览",
            width: "800px",
            component: "window-print-preview",
            form: {
              value: 1,
              row,
              print,
            },
            ok: () => {
              this.modalObj.show = false;
            },
            cancel: () => {
              this.modalObj.show = false;
            },
          };
        });
      });
    },
    getOrderDetail(row) {
      //查询详情
      return getTranOrde(row.tranOrderId).then((response) => {
        return {
          response,
          // remark: row.remark,
          // receiptOrderNo: row.tranOrderCode,
          // details:response.tranOrderLineList
          // createTime: row.createTime,
          // details: detailList,
          // totalCount
        };
      });
    },
    /** 查询颜色列表 */
    getColorList() {
      totalListColor().then((response) => {
        this.colorList = response.data;
      });
    },
    //销售单列表选择
    handleSelectSalesItem(obj) {
      if (this.form.warehouseId == undefined || this.form.warehouseId == null) {
        this.$modal.msgError("请先选择仓库");
        return;
      }
      if (this.form.clientName != undefined && this.form.clientName != null) {
        this.$refs.salesItemSelect.showFlag = true;
        this.$refs.salesItemSelect.queryParams.clientId = obj.clientId;
        this.$refs.salesItemSelect.queryParams.clientCode = obj.clientCode;
        this.$refs.salesItemSelect.queryParams.clientName = obj.clientName;
        this.$refs.salesItemSelect.warehouseList = this.warehouseList;
        this.$refs.salesItemSelect.queryParams.warehouseId = obj.warehouseId;
        this.$refs.salesItemSelect.getList();
      } else {
        this.$modal.msgError("请先选择客户");
      }
    },
    //销售单列表选择弹出框
    onSalesItemSelect(obj) {
      if (obj != undefined && obj != null) {
        obj.map((item) => {
          this.currentRow = item;
          this.currentRow.salesOrderId = item.salesOrderItemId;
          this.currentRow.salesOrderCode = item.salesOrderItemCode;
          this.currentRow.clientId = this.form.clientId;
          this.currentRow.clientCode = this.form.clientCode;
          this.currentRow.clientName = this.form.clientName;
          this.currentRow.tranDate = this.form.tranDate;
          this.currentRow.tranOrderType = this.form.tranOrderType;
          this.currentRow.warehouseId = this.form.warehouseId;
          this.currentRow.saleQty = item.salesOrderQuantity;
          this.currentRow.saleSgqty = item.oweQty;
          this.tranOrderLineList.push(this.currentRow);
        });
      }
    },
    //计算金额
    changeAmount(row) {
      if (
        (row.saleSgqty != null || row.saleSgqty != undefined) &&
        (row.amount != null || row.amount != undefined)
      ) {
        row.totalAmount = row.amount * row.saleSgqty;
      }
      let totalPirce = 0;

      this.tranOrderLineList.map((item) => {
        if (item.amount != null &&  item.amount != undefined) {
          totalPirce += item.totalAmount;
        }
      this.form.totalAmount = parseInt(totalPirce);
      });
    },

    //计算总数
    changeTotal(row) {
      let total = 0;
      this.tranOrderLineList.map((item) => {
        if (item.saleSgqty != null &&  item.saleSgqty != undefined) {
          total += parseInt(item.saleSgqty);
        }
      });
      this.form.total = total;
    },
    //计算总重量
    changeWeight(row) {
      let totalWeight = 0;
      this.tranOrderLineList.map((item) => {
         if (item.weight != null && item.weight != undefined) {
          totalWeight += parseInt(item.weight);
        }
      });
      this.form.weight = totalWeight;
    },
    //计算总件数
    changePicNum(row) {
      let totalPicNum = 0;
      this.tranOrderLineList.map((item) => {
        if (item.picNum != null &&  item.picNum != undefined) {
          totalPicNum += parseInt(item.picNum);
        }
      });
      this.form.totalPic = totalPicNum;
    },

    /** 查询销售送货单列表 */
    getList() {
      this.loading = true;
      listTranOrde(this.queryParams).then((response) => {
        this.tranOrdeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    //物料选择弹出框
    onItemSelected(obj) {
      if (obj != undefined && obj != null) {
        // 先删除原有数据
        let index = this.tranOrderLineList.indexOf(this.currentRow);
        this.tranOrderLineList.splice(index, 1);
        this.currentRow.productId = obj.itemId;
        this.currentRow.productCode = obj.itemCode;
        this.currentRow.productName = obj.itemName;
        this.currentRow.quality = obj.quality;
        this.currentRow.unitOfMeasure = obj.unitOfMeasure;
        // 添加到表格
        this.tranOrderLineList.push(this.currentRow);
      }
    },
    handleSelectProduct() {
      this.$refs.itemSelect.showFlag = true;
    },
    //获取仓库信息
    getWarehouseList() {
      listWarehouse().then((response) => {
        this.warehouseList = response.rows;
      });
    },
    //自动生成编码
    handleAutoGenChange(autoGenFlag) {
      if (autoGenFlag) {
        genCode("PRODUCTSALSE_CODE").then((response) => {
          this.form.tranOrderCode = response;
        });
      } else {
        this.form.tranOrderCode = null;
      }
    },
    handleSelectClient() {
      this.$refs.clientSelect.showFlag = true;
    },
    //客户选择弹出框
    onClientSelected(obj) {
      if (obj != undefined && obj != null) {
        this.form.clientId = obj.clientId;
        this.form.clientCode = obj.clientCode;
        this.form.clientName = obj.clientName;

        this.form.currency = obj.currency;
        this.form.payUp = obj.payUp;
        this.form.followerMan = obj.followerMan;
        this.form.busMan = obj.salesMan;
      }
    },
    /**行单击 */
    handleRowClick(row, event, column) {
      this.currentRow = row;
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        tranOrderId: null,
        tranOrderCode: null,
        warehouseId: null,
        tranOrderType: null,
        status: "PREPARE",
        address: null,
        clientId: null,
        clientCode: null,
        clientName: null,
        totalAmount: null,
        weight: null,
        total: null,
        totalPic: null,
        currency: null,
        payUp: null,
        followerMan: null,
        busMan: null,
        attr1: null,
        attr2: null,
        attr3: null,
        attr4: null,
        createUserId: null,
        createBy: null,
        createTime: null,
        updateUserId: null,
        updateBy: null,
        updateTime: null,
        remark: null,
      };

      this.autoGenFlag = false;
      this.tranOrderLineList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.tranOrderId);
      this.codes = selection.map((item) => item.tranOrderCode);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getColorList();
      this.reset();
      this.open = true;
      this.title = "添加销售送货单";
      this.optType = "add";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getColorList();
      const tranOrderId = row.tranOrderId || this.ids;
      getTranOrde(tranOrderId).then((response) => {
        this.form = response.data;
        this.tranOrderLineList = response.data.tranOrderLineList;
        this.open = true;
        this.title = "修改销售送货单";
        this.optType = "edit";
      });
    },
    // 查询明细按钮操作
    handleView(row) {
      this.reset();
      const tranOrderId = row.tranOrderId || this.ids;
      getTranOrde(tranOrderId).then((response) => {
        this.form = response.data;
        this.tranOrderLineList = response.data.tranOrderLineList;
        this.open = true;
        this.title = "销售送货单详情";
        this.optType = "view";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.form.tranOrderLineList = this.tranOrderLineList;
          if (this.form.tranOrderId != null) {
            updateTranOrde(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTranOrde(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const tranOrderIds = row.tranOrderId || this.ids;
      const tranOrderCodes = row.tranOrderCode || this.codes;
      this.$modal
        .confirm(
          '是否确认删除销售送货单编号为"' + tranOrderCodes + '"的数据项？'
        )
        .then(function () {
          return delTranOrde(tranOrderIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    // 确认
    handleConfirm(status) {
      let that = this;
      this.$modal.confirm("是否确认？【确认后将不能更改】").then(function () {
        if (status == "CONFIRMED") {
          refuse(that.form.tranOrderId, status).then((response) => {
            that.$modal.msgSuccess("提交成功");
            that.open = false;
            that.getList();
          });
        }
        if (status == "APPROVING") {
          that.form.status = "APPROVING";
          updateTranOrde(that.form).then((response) => {
            that.$modal.msgSuccess(response.msg);
            that.open = false;
            that.getList();
          });
        }
      });
    },
    //提交审批
    handleSubmit() {
      let that = this;
      refuse(this.form.tranOrderId, "APPROVING").then((response) => {
        that.$modal.msgSuccess(response.msg);
        that.open = false;
        that.getList();
      });
    },
    //审批通过执行
    handleExecute() {
      let that = this;
      const tranOrderId = that.form.tranOrderId;
      that.$modal
        .confirm("通过后会生成执行出库,是否确认通过？")
        .then(function () {
          execute(tranOrderId).then((response) => {
            that.$modal.msgSuccess(response.msg);
            ththatis.open = false;
            that.getList();
          });
        });
    },
    //审批不通过，拒绝
    handleReject() {
      refuse(this.form.tranOrderId, "REFUSE").then((response) => {
        this.$modal.msgSuccess(response.msg);
        this.open = false;
        this.getList();
      });
    },
    /** 销售送货单列序号 */
    rowTranOrdeLineIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 销售送货单列添加按钮操作 */
    handleAddTranOrderLine(sales) {
      let obj = {};
      if (this.form.clientName == undefined || this.form.clientName == null) {
        this.$modal.msgError("请先选择客户");
        return;
      }
      obj.clientId = sales.clientId;
      obj.clientCode = sales.clientCode;
      obj.clientName = sales.clientName;
      obj.warehouseId = sales.warehouseId;
      obj.busMan = sales.busMan;
      obj.followerMan = sales.followerMan;
      obj.tranOrderType = sales.tranOrderType;
      obj.tranOrderLineId = "";
      obj.tranDate = sales.tranDate;
      obj.tranCode = "";
      obj.warehouseCode = "";
      obj.warehouseName = "";
      obj.salesOrderId = "";
      obj.salesOrderCode = "";
      obj.productId = "";
      obj.productCode = "";
      obj.productName = "";
      obj.productSpec = "";
      obj.quality = "";
      obj.level = "";
      obj.colorName = "";
      obj.unitOfMeasure = "";
      obj.saleQty = "";
      obj.saleThqty = "";
      obj.amount = "";
      obj.saleSgqty = "";
      obj.weight = "";
      obj.tax = "";
      obj.totalAmount = "";
      obj.extra = "";
      obj.remark = "";
      obj.picNum = "";
      obj.po = "";
      obj.sku = "";
      obj.clientSpec = "";
      obj.clientProductName = "";
      obj.clientColor = "";
      obj.workorderCode = "";
      obj.status = "";
      obj.createBy = "";
      obj.createTime = "";
      obj.updateBy = "";
      obj.updateTime = "";
      this.tranOrderLineList.push(obj);
    },
    /** 销售送货单列删除按钮操作 */
    handleDeleteTranOrdeLine() {
      if (this.checkedTranOrdeLine.length == 0) {
        this.$modal.msgError("请先选择要删除的销售送货单列数据");
      } else {
        if (this.itemIds.length > 0) {
          delOrderItem(this.itemIds).then((response) => {
            this.$modal.msgSuccess(response.msg);
          });
        }
        const tranOrderLineList = this.tranOrderLineList;
        const checkedTranOrdeLine = this.checkedTranOrdeLine;
        this.tranOrderLineList = tranOrderLineList.filter(function (item) {
          return checkedTranOrdeLine.indexOf(item.index) == -1;
        });
      }
    },
    /** 复选框选中数据 */
    handleTranOrdeLineSelectionChange(selection) {
      this.checkedTranOrdeLine = selection.map((item) => item.index);
      this.itemIds = selection.map((item) => {
        if (
          item.salesOrderItemId != null &&
          (item.salesOrderItemId != undefined) & (item.salesOrderItemId != "")
        ) {
          return item.salesOrderItemId;
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "md/tranOrder/export",
        {
          ...this.queryParams,
        },
        `tran_orde_${new Date().getTime()}.xlsx`
      );
    },
    //动态加载数据
    handleRouteChange() {
      this.optType = this.$route.params.optType;
      const tranOrderId = this.$route.params.tranOrderId;
      if (this.optType === "view") {
        this.open = false;
        this.reset();
        getTranOrde(tranOrderId).then((response) => {
          this.form = response.data;
          this.tranOrderLineList = response.data.tranOrderLineList;
          this.open = true;
          this.title = "销售送货单详情";
          this.optType = "view";
        });
      }
    },
    activated() {
      this.optType = this.$route.params.optType;
      this.handleRouteChange();
    },
  },
};
</script>
