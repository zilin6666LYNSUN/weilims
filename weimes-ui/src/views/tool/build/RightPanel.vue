<template>
  <div class="right-board">
    <el-tabs v-model="currentTab" class="center-tabs">
      <el-tab-pane :label="$t('tool.组件属性')" name="field" />
      <el-tab-pane :label="$t('tool.表单属性')" name="form" />
    </el-tabs>
    <div class="field-box">
      <a class="document-link" target="_blank" :href="documentLink" :title="$t('tool.查看组件文档')">
        <i class="el-icon-link" />
      </a>
      <el-scrollbar class="right-scrollbar">
        <!-- 组件属性 -->
        <el-form v-show="currentTab==='field' && showField" size="small" label-width="90px">
          <el-form-item v-if="activeData.changeTag" :label="$t('tool.组件类型')">
            <el-select
              v-model="activeData.tagIcon"
              :placeholder="$t('tool.请选择组件类型')"
              :style="{width: '100%'}"
              @change="tagChange"
            >
              <el-option-group v-for="group in tagList" :key="group.label" :label="group.label">
                <el-option
                  v-for="item in group.options"
                  :key="item.label"
                  :label="item.label"
                  :value="item.tagIcon"
                >
                  <svg-icon class="node-icon" :icon-class="item.tagIcon" />
                  <span> {{ item.label }}</span>
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.vModel!==undefined" :label="$t('tool.字段名')">
            <el-input v-model="activeData.vModel" :placeholder="$t('tool.请输入字段名（v-model）')" />
          </el-form-item>
          <el-form-item v-if="activeData.componentName!==undefined" :label="$t('tool.组件名')">
            {{ activeData.componentName }}
          </el-form-item>
          <el-form-item v-if="activeData.label!==undefined" :label="$t('system.标题')">
            <el-input v-model="activeData.label" :placeholder="$t('system.请输入标题')" />
          </el-form-item>
          <el-form-item v-if="activeData.placeholder!==undefined" :label="$t('tool.占位提示')">
            <el-input v-model="activeData.placeholder" :placeholder="$t('tool.请输入占位提示')" />
          </el-form-item>
          <el-form-item v-if="activeData['start-placeholder']!==undefined" :label="$t('tool.开始占位')">
            <el-input v-model="activeData['start-placeholder']" :placeholder="$t('tool.请输入占位提示')" />
          </el-form-item>
          <el-form-item v-if="activeData['end-placeholder']!==undefined" :label="$t('tool.结束占位')">
            <el-input v-model="activeData['end-placeholder']" :placeholder="$t('tool.请输入占位提示')" />
          </el-form-item>
          <el-form-item v-if="activeData.span!==undefined" :label="$t('tool.表单栅格')">
            <el-slider v-model="activeData.span" :max="24" :min="1" :marks="{12:''}" @change="spanChange" />
          </el-form-item>
          <el-form-item v-if="activeData.layout==='rowFormItem'" :label="$t('tool.栅格间隔')">
            <el-input-number v-model="activeData.gutter" :min="0" :placeholder="$t('tool.栅格间隔')" />
          </el-form-item>
          <el-form-item v-if="activeData.layout==='rowFormItem'" :label="$t('tool.布局模式')">
            <el-radio-group v-model="activeData.type">
              <el-radio-button label="default" />
              <el-radio-button label="flex" />
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.justify!==undefined&&activeData.type==='flex'" :label="$t('tool.水平排列')">
            <el-select v-model="activeData.justify" :placeholder="$t('tool.请选择水平排列')" :style="{width: '100%'}">
              <el-option
                v-for="(item, index) in justifyOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.align!==undefined&&activeData.type==='flex'" :label="$t('tool.垂直排列')">
            <el-radio-group v-model="activeData.align">
              <el-radio-button label="top" />
              <el-radio-button label="middle" />
              <el-radio-button label="bottom" />
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.labelWidth!==undefined" :label="$t('tool.标签宽度')">
            <el-input v-model.number="activeData.labelWidth" type="number" :placeholder="$t('tool.请输入标签宽度')" />
          </el-form-item>
          <el-form-item v-if="activeData.style&&activeData.style.width!==undefined" :label="$t('tool.组件宽度')">
            <el-input v-model="activeData.style.width" :placeholder="$t('tool.请输入组件宽度')" clearable />
          </el-form-item>
          <el-form-item v-if="activeData.vModel!==undefined" :label="$t('tool.默认值')">
            <el-input
              :value="setDefaultValue(activeData.defaultValue)"
              :placeholder="$t('tool.请输入默认值')"
              @input="onDefaultValueInput"
            />
          </el-form-item>
          <el-form-item v-if="activeData.tag==='el-checkbox-group'" :label="$t('tool.至少应选')">
            <el-input-number
              :value="activeData.min"
              :min="0"
              :placeholder="$t('tool.至少应选')"
              @input="$set(activeData, 'min', $event?$event:undefined)"
            />
          </el-form-item>
          <el-form-item v-if="activeData.tag==='el-checkbox-group'" :label="$t('tool.最多可选')">
            <el-input-number
              :value="activeData.max"
              :min="0"
              :placeholder="$t('tool.最多可选')"
              @input="$set(activeData, 'max', $event?$event:undefined)"
            />
          </el-form-item>
          <el-form-item v-if="activeData.prepend!==undefined" :label="$t('tool.前缀')">
            <el-input v-model="activeData.prepend" :placeholder="$t('tool.请输入前缀')" />
          </el-form-item>
          <el-form-item v-if="activeData.append!==undefined" :label="$t('tool.后缀')">
            <el-input v-model="activeData.append" :placeholder="$t('tool.请输入后缀')" />
          </el-form-item>
          <el-form-item v-if="activeData['prefix-icon']!==undefined" :label="$t('tool.前图标')">
            <el-input v-model="activeData['prefix-icon']" :placeholder="$t('tool.请输入前图标名称')">
              <el-button slot="append" icon="el-icon-thumb" @click="openIconsDialog('prefix-icon')">
                {{ this.$t('system.选择') }}
              </el-button>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData['suffix-icon'] !== undefined" :label="$t('tool.后图标')">
            <el-input v-model="activeData['suffix-icon']" :placeholder="$t('tool.请输入后图标名称')">
              <el-button slot="append" icon="el-icon-thumb" @click="openIconsDialog('suffix-icon')">
                {{ this.$t('system.选择') }}
              </el-button>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" :label="$t('tool.选项分隔符')">
            <el-input v-model="activeData.separator" :placeholder="$t('tool.请输入选项分隔符')" />
          </el-form-item>
          <el-form-item v-if="activeData.autosize !== undefined" :label="$t('tool.最小行数')">
            <el-input-number v-model="activeData.autosize.minRows" :min="1" :placeholder="$t('tool.最小行数')" />
          </el-form-item>
          <el-form-item v-if="activeData.autosize !== undefined" :label="$t('tool.最大行数')">
            <el-input-number v-model="activeData.autosize.maxRows" :min="1" :placeholder="$t('tool.最大行数')" />
          </el-form-item>
          <el-form-item v-if="activeData.min !== undefined" :label="$t('tool.最小值')">
            <el-input-number v-model="activeData.min" :placeholder="$t('tool.最小值')" />
          </el-form-item>
          <el-form-item v-if="activeData.max !== undefined" :label="$t('tool.最大值')">
            <el-input-number v-model="activeData.max" :placeholder="$t('tool.最大值')" />
          </el-form-item>
          <el-form-item v-if="activeData.step !== undefined" :label="$t('tool.步长')">
            <el-input-number v-model="activeData.step" :placeholder="$t('tool.步数')" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" :label="$t('tool.精度')">
            <el-input-number v-model="activeData.precision" :min="0" :placeholder="$t('tool.精度')" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" :label="$t('tool.按钮位置')">
            <el-radio-group v-model="activeData['controls-position']">
              <el-radio-button label="">
                {{ this.$t('system.默认') }}
              </el-radio-button>
              <el-radio-button label="right">
                {{ this.$t('tool.右侧') }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.maxlength !== undefined" :label="$t('tool.最多输入')">
            <el-input v-model="activeData.maxlength" :placeholder="$t('tool.请输入字符长度')">
              <template slot="append">
                {{ this.$t('tool.个字符') }}
              </template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData['active-text'] !== undefined" :label="$t('tool.开启提示')">
            <el-input v-model="activeData['active-text']" :placeholder="$t('tool.请输入开启提示')" />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-text'] !== undefined" :label="$t('tool.关闭提示')">
            <el-input v-model="activeData['inactive-text']" :placeholder="$t('tool.请输入关闭提示')" />
          </el-form-item>
          <el-form-item v-if="activeData['active-value'] !== undefined" :label="$t('tool.开启值')">
            <el-input
              :value="setDefaultValue(activeData['active-value'])"
              :placeholder="$t('tool.请输入开启值')"
              @input="onSwitchValueInput($event, 'active-value')"
            />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-value'] !== undefined" :label="$t('tool.关闭值')">
            <el-input
              :value="setDefaultValue(activeData['inactive-value'])"
              :placeholder="$t('tool.请输入关闭值')"
              @input="onSwitchValueInput($event, 'inactive-value')"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.type !== undefined && 'el-date-picker' === activeData.tag"
            :label="$t('tool.时间类型')"
          >
            <el-select
              v-model="activeData.type"
              :placeholder="$t('tool.请选择时间类型')"
              :style="{ width: '100%' }"
              @change="dateTypeChange"
            >
              <el-option
                v-for="(item, index) in dateOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.name !== undefined" :label="$t('tool.文件字段名')">
            <el-input v-model="activeData.name" :placeholder="$t('tool.请输入上传文件字段名')" />
          </el-form-item>
          <el-form-item v-if="activeData.accept !== undefined" :label="$t('tool.文件类型')">
            <el-select
              v-model="activeData.accept"
              :placeholder="$t('tool.请选择文件类型')"
              :style="{ width: '100%' }"
              clearable
            >
              <el-option :label="$t('tool.图片')" value="image/*" />
              <el-option :label="$t('tool.视频')" value="video/*" />
              <el-option :label="$t('tool.音频')" value="audio/*" />
              <el-option label="excel" value=".xls,.xlsx" />
              <el-option label="word" value=".doc,.docx" />
              <el-option label="pdf" value=".pdf" />
              <el-option label="txt" value=".txt" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.fileSize !== undefined" :label="$t('tool.文件大小')">
            <el-input v-model.number="activeData.fileSize" :placeholder="$t('tool.请输入文件大小')">
              <el-select slot="append" v-model="activeData.sizeUnit" :style="{ width: '66px' }">
                <el-option label="KB" value="KB" />
                <el-option label="MB" value="MB" />
                <el-option label="GB" value="GB" />
              </el-select>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData.action !== undefined" :label="$t('tool.上传地址')">
            <el-input v-model="activeData.action" :placeholder="$t('tool.请输入上传地址')" clearable />
          </el-form-item>
          <el-form-item v-if="activeData['list-type'] !== undefined" :label="$t('tool.列表类型')">
            <el-radio-group v-model="activeData['list-type']" size="small">
              <el-radio-button label="text">
                text
              </el-radio-button>
              <el-radio-button label="picture">
                picture
              </el-radio-button>
              <el-radio-button label="picture-card">
                picture-card
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData.buttonText !== undefined"
            v-show="'picture-card' !== activeData['list-type']"
            :label="$t('tool.按钮文字')"
          >
            <el-input v-model="activeData.buttonText" :placeholder="$t('tool.请输入按钮文字')" />
          </el-form-item>
          <el-form-item v-if="activeData['range-separator'] !== undefined" :label="$t('tool.分隔符')">
            <el-input v-model="activeData['range-separator']" :placeholder="$t('tool.请输入分隔符')" />
          </el-form-item>
          <el-form-item v-if="activeData['picker-options'] !== undefined" :label="$t('tool.时间段')">
            <el-input
              v-model="activeData['picker-options'].selectableRange"
              :placeholder="$t('tool.请输入时间段')"
            />
          </el-form-item>
          <el-form-item v-if="activeData.format !== undefined" :label="$t('tool.时间格式')">
            <el-input
              :value="activeData.format"
              :placeholder="$t('tool.请输入时间格式')"
              @input="setTimeValue($event)"
            />
          </el-form-item>
          <template v-if="['el-checkbox-group', 'el-radio-group', 'el-select'].indexOf(activeData.tag) > -1">
            <el-divider>{{ this.$t('tool.选项') }}</el-divider>
            <draggable
              :list="activeData.options"
              :animation="340"
              group="selectItem"
              handle=".option-drag"
            >
              <div v-for="(item, index) in activeData.options" :key="index" class="select-item">
                <div class="select-line-icon option-drag">
                  <i class="el-icon-s-operation" />
                </div>
                <el-input v-model="item.label" :placeholder="$t('tool.选项名')" size="small" />
                <el-input
                  :placeholder="$t('tool.选项值')"
                  size="small"
                  :value="item.value"
                  @input="setOptionValue(item, $event)"
                />
                <div class="close-btn select-line-icon" @click="activeData.options.splice(index, 1)">
                  <i class="el-icon-remove-outline" />
                </div>
              </div>
            </draggable>
            <div style="margin-left: 20px;">
              <el-button
                style="padding-bottom: 0"
                icon="el-icon-circle-plus-outline"
                type="text"
                @click="addSelectItem"
              >
                {{ this.$t('tool.添加选项') }}
              </el-button>
            </div>
            <el-divider />
          </template>

          <template v-if="['el-cascader'].indexOf(activeData.tag) > -1">
            <el-divider>{{ this.$t('tool.选项') }}</el-divider>
            <el-form-item :label="$t('tool.数据类型')">
              <el-radio-group v-model="activeData.dataType" size="small">
                <el-radio-button label="dynamic">
                  {{ this.$t('tool.动态数据') }}
                </el-radio-button>
                <el-radio-button label="static">
                  {{ this.$t('tool.静态数据') }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <template v-if="activeData.dataType === 'dynamic'">
              <el-form-item :label="$t('tool.标签键名')">
                <el-input v-model="activeData.labelKey" :placeholder="$t('tool.请输入标签键名')" />
              </el-form-item>
              <el-form-item :label="$t('tool.值键名')">
                <el-input v-model="activeData.valueKey" :placeholder="$t('tool.请输入值键名')" />
              </el-form-item>
              <el-form-item :label="$t('tool.子级键名')">
                <el-input v-model="activeData.childrenKey" :placeholder="$t('tool.请输入子级键名')" />
              </el-form-item>
            </template>

            <el-tree
              v-if="activeData.dataType === 'static'"
              draggable
              :data="activeData.options"
              node-key="id"
              :expand-on-click-node="false"
              :render-content="renderContent"
            />
            <div v-if="activeData.dataType === 'static'" style="margin-left: 20px">
              <el-button
                style="padding-bottom: 0"
                icon="el-icon-circle-plus-outline"
                type="text"
                @click="addTreeItem"
              >
                {{ this.$t('tool.添加父级') }}
              </el-button>
            </div>
            <el-divider />
          </template>

          <el-form-item v-if="activeData.optionType !== undefined" :label="$t('tool.选项样式')">
            <el-radio-group v-model="activeData.optionType">
              <el-radio-button label="default">
                {{ this.$t('system.默认') }}
              </el-radio-button>
              <el-radio-button label="button">
                {{ this.$t('system.按钮') }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData['active-color'] !== undefined" :label="$t('tool.开启颜色')">
            <el-color-picker v-model="activeData['active-color']" />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-color'] !== undefined" :label="$t('tool.关闭颜色')">
            <el-color-picker v-model="activeData['inactive-color']" />
          </el-form-item>

          <el-form-item v-if="activeData['allow-half'] !== undefined" :label="$t('tool.允许半选')">
            <el-switch v-model="activeData['allow-half']" />
          </el-form-item>
          <el-form-item v-if="activeData['show-text'] !== undefined" :label="$t('tool.辅助文字')">
            <el-switch v-model="activeData['show-text']" @change="rateTextChange" />
          </el-form-item>
          <el-form-item v-if="activeData['show-score'] !== undefined" :label="$t('tool.显示分数')">
            <el-switch v-model="activeData['show-score']" @change="rateScoreChange" />
          </el-form-item>
          <el-form-item v-if="activeData['show-stops'] !== undefined" :label="$t('tool.显示间断点')">
            <el-switch v-model="activeData['show-stops']" />
          </el-form-item>
          <el-form-item v-if="activeData.range !== undefined" :label="$t('tool.范围选择')">
            <el-switch v-model="activeData.range" @change="rangeChange" />
          </el-form-item>
          <el-form-item
            v-if="activeData.border !== undefined && activeData.optionType === 'default'"
            :label="$t('tool.是否带边框')"
          >
            <el-switch v-model="activeData.border" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-color-picker'" :label="$t('tool.颜色格式')">
            <el-select
              v-model="activeData['color-format']"
              :placeholder="$t('tool.请选择颜色格式')"
              :style="{ width: '100%' }"
              @change="colorFormatChange"
            >
              <el-option
                v-for="(item, index) in colorFormatOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="activeData.size !== undefined &&
              (activeData.optionType === 'button' ||
                activeData.border ||
                activeData.tag === 'el-color-picker')"
            :label="$t('tool.选项尺寸')"
          >
            <el-radio-group v-model="activeData.size">
              <el-radio-button label="medium">
                {{ this.$t('tool.中等') }}
              </el-radio-button>
              <el-radio-button label="small">
                {{ this.$t('tool.较小') }}
              </el-radio-button>
              <el-radio-button label="mini">
                {{ this.$t('tool.迷你') }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData['show-word-limit'] !== undefined" :label="$t('tool.输入统计')">
            <el-switch v-model="activeData['show-word-limit']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" :label="$t('tool.严格步数')">
            <el-switch v-model="activeData['step-strictly']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" :label="$t('tool.是否多选')">
            <el-switch v-model="activeData.props.props.multiple" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" :label="$t('tool.展示全路径')">
            <el-switch v-model="activeData['show-all-levels']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" :label="$t('tool.可否筛选')">
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item v-if="activeData.clearable !== undefined" :label="$t('tool.能否清空')">
            <el-switch v-model="activeData.clearable" />
          </el-form-item>
          <el-form-item v-if="activeData.showTip !== undefined" :label="$t('tool.显示提示')">
            <el-switch v-model="activeData.showTip" />
          </el-form-item>
          <el-form-item v-if="activeData.multiple !== undefined" :label="$t('tool.多选文件')">
            <el-switch v-model="activeData.multiple" />
          </el-form-item>
          <el-form-item v-if="activeData['auto-upload'] !== undefined" :label="$t('tool.自动上传')">
            <el-switch v-model="activeData['auto-upload']" />
          </el-form-item>
          <el-form-item v-if="activeData.readonly !== undefined" :label="$t('tool.是否只读')">
            <el-switch v-model="activeData.readonly" />
          </el-form-item>
          <el-form-item v-if="activeData.disabled !== undefined" :label="$t('tool.是否禁用')">
            <el-switch v-model="activeData.disabled" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-select'" :label="$t('tool.是否可搜索')">
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-select'" :label="$t('tool.是否多选')">
            <el-switch v-model="activeData.multiple" @change="multipleChange" />
          </el-form-item>
          <el-form-item v-if="activeData.required !== undefined" :label="$t('tool.是否必填')">
            <el-switch v-model="activeData.required" />
          </el-form-item>

          <template v-if="activeData.layoutTree">
            <el-divider>{{ this.$t('tool.布局结构树') }}</el-divider>
            <el-tree
              :data="[activeData]"
              :props="layoutTreeProps"
              node-key="renderKey"
              default-expand-all
              draggable
            >
              <span slot-scope="{ node, data }">
                <span class="node-label">
                  <svg-icon class="node-icon" :icon-class="data.tagIcon" />
                  {{ node.label }}
                </span>
              </span>
            </el-tree>
          </template>

          <template v-if="activeData.layout === 'colFormItem'">
            <el-divider>{{ this.$t('tool.正则校验') }}</el-divider>
            <div
              v-for="(item, index) in activeData.regList"
              :key="index"
              class="reg-item"
            >
              <span class="close-btn" @click="activeData.regList.splice(index, 1)">
                <i class="el-icon-close" />
              </span>
              <el-form-item :label="$t('tool.表达式')">
                <el-input v-model="item.pattern" :placeholder="$t('tool.请输入正则')" />
              </el-form-item>
              <el-form-item :label="$t('lims.错误提示')" style="margin-bottom:0">
                <el-input v-model="item.message" :placeholder="$t('tool.请输入错误提示')" />
              </el-form-item>
            </div>
            <div style="margin-left: 20px">
              <el-button icon="el-icon-circle-plus-outline" type="text" @click="addReg">
                {{ this.$t('tool.添加规则') }}
              </el-button>
            </div>
          </template>
        </el-form>
        <!-- 表单属性 -->
        <el-form v-show="currentTab === 'form'" size="small" label-width="90px">
          <el-form-item :label="$t('tool.表单名')">
            <el-input v-model="formConf.formRef" :placeholder="$t('tool.请输入表单名（ref）')" />
          </el-form-item>
          <el-form-item :label="$t('tool.表单模型')">
            <el-input v-model="formConf.formModel" :placeholder="$t('tool.请输入数据模型')" />
          </el-form-item>
          <el-form-item :label="$t('tool.校验模型')">
            <el-input v-model="formConf.formRules" :placeholder="$t('tool.请输入校验模型')" />
          </el-form-item>
          <el-form-item :label="$t('tool.表单尺寸')">
            <el-radio-group v-model="formConf.size">
              <el-radio-button label="medium">
                {{ this.$t('tool.中等') }}
              </el-radio-button>
              <el-radio-button label="small">
                {{ this.$t('tool.较小') }}
              </el-radio-button>
              <el-radio-button label="mini">
                {{ this.$t('tool.迷你') }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('tool.标签对齐')">
            <el-radio-group v-model="formConf.labelPosition">
              <el-radio-button label="left">
                {{ this.$t('tool.左对齐') }}
              </el-radio-button>
              <el-radio-button label="right">
                {{ this.$t('tool.右对齐') }}
              </el-radio-button>
              <el-radio-button label="top">
                {{ this.$t('tool.顶部对齐') }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('tool.标签宽度')">
            <el-input-number v-model="formConf.labelWidth" :placeholder="$t('tool.标签宽度')" />
          </el-form-item>
          <el-form-item :label="$t('tool.栅格间隔')">
            <el-input-number v-model="formConf.gutter" :min="0" :placeholder="$t('tool.栅格间隔')" />
          </el-form-item>
          <el-form-item :label="$t('tool.禁用表单')">
            <el-switch v-model="formConf.disabled" />
          </el-form-item>
          <el-form-item :label="$t('tool.表单按钮')">
            <el-switch v-model="formConf.formBtns" />
          </el-form-item>
          <el-form-item :label="$t('tool.显示未选中组件边框')">
            <el-switch v-model="formConf.unFocusedComponentBorder" />
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>

    <treeNode-dialog :visible.sync="dialogVisible" :title="$t('tool.添加选项')" @commit="addNode" />
    <icons-dialog :visible.sync="iconsVisible" :current="activeData[currentIconModel]" @select="setIcon" />
  </div>
</template>

<script>
import { isArray } from 'util'
import draggable from 'vuedraggable'
import TreeNodeDialog from './TreeNodeDialog'
import { isNumberStr } from '@/utils/index'
import IconsDialog from './IconsDialog'
import {
  inputComponents,
  selectComponents,
  layoutComponents
} from '@/utils/generator/config'

const dateTimeFormat = {
  date: 'yyyy-MM-dd',
  month: 'yyyy-MM',
  year: 'yyyy',
  datetime: 'yyyy-MM-dd HH:mm:ss',
  daterange: 'yyyy-MM-dd',
  monthrange: 'yyyy-MM',
  datetimerange: 'yyyy-MM-dd HH:mm:ss'
}

export default {
  components: {
    draggable,
    TreeNodeDialog,
    IconsDialog
  },
  props: ['showField', 'activeData', 'formConf'],
  data() {
    return {
      currentTab: 'field',
      currentNode: null,
      dialogVisible: false,
      iconsVisible: false,
      currentIconModel: null,
      dateTypeOptions: [
        {
          label: this.$t('tool.日(date)'),
          value: 'date'
        },
        {
          label: this.$t('tool.周(week)'),
          value: 'week'
        },
        {
          label: this.$t('tool.月(month)'),
          value: 'month'
        },
        {
          label: this.$t('tool.年(year)'),
          value: 'year'
        },
        {
          label: this.$t('tool.日期时间(datetime)'),
          value: 'datetime'
        }
      ],
      dateRangeTypeOptions: [
        {
          label: this.$t('tool.日期范围(daterange)'),
          value: 'daterange'
        },
        {
          label: this.$t('tool.月范围(monthrange)'),
          value: 'monthrange'
        },
        {
          label: this.$t('tool.日期时间范围(datetimerange)'),
          value: 'datetimerange'
        }
      ],
      colorFormatOptions: [
        {
          label: 'hex',
          value: 'hex'
        },
        {
          label: 'rgb',
          value: 'rgb'
        },
        {
          label: 'rgba',
          value: 'rgba'
        },
        {
          label: 'hsv',
          value: 'hsv'
        },
        {
          label: 'hsl',
          value: 'hsl'
        }
      ],
      justifyOptions: [
        {
          label: 'start',
          value: 'start'
        },
        {
          label: 'end',
          value: 'end'
        },
        {
          label: 'center',
          value: 'center'
        },
        {
          label: 'space-around',
          value: 'space-around'
        },
        {
          label: 'space-between',
          value: 'space-between'
        }
      ],
      layoutTreeProps: {
        label(data, node) {
          return data.componentName || `${data.label}: ${data.vModel}`
        }
      }
    }
  },
  computed: {
    documentLink() {
      return (
        this.activeData.document
        || 'https://element.eleme.cn/#/zh-CN/component/installation'
      )
    },
    dateOptions() {
      if (
        this.activeData.type !== undefined
        && this.activeData.tag === 'el-date-picker'
      ) {
        if (this.activeData['start-placeholder'] === undefined) {
          return this.dateTypeOptions
        }
        return this.dateRangeTypeOptions
      }
      return []
    },
    tagList() {
      return [
        {
          label: this.$t('tool.输入型组件'),
          options: inputComponents
        },
        {
          label: this.$t('tool.选择型组件'),
          options: selectComponents
        }
      ]
    }
  },
  methods: {
    addReg() {
      this.activeData.regList.push({
        pattern: '',
        message: ''
      })
    },
    addSelectItem() {
      this.activeData.options.push({
        label: '',
        value: ''
      })
    },
    addTreeItem() {
      ++this.idGlobal
      this.dialogVisible = true
      this.currentNode = this.activeData.options
    },
    renderContent(h, { node, data, store }) {
      return (
        <div class="custom-tree-node">
          <span>{node.label}</span>
          <span class="node-operation">
            <i on-click={() => this.append(data)}
              class="el-icon-plus"
              title={this.$t('lims.添加')}
            ></i>
            <i on-click={() => this.remove(node, data)}
              class="el-icon-delete"
              title={this.$t('common.delete')}
            ></i>
          </span>
        </div>
      )
    },
    append(data) {
      if (!data.children) {
        this.$set(data, 'children', [])
      }
      this.dialogVisible = true
      this.currentNode = data.children
    },
    remove(node, data) {
      const { parent } = node
      const children = parent.data.children || parent.data
      const index = children.findIndex(d => d.id === data.id)
      children.splice(index, 1)
    },
    addNode(data) {
      this.currentNode.push(data)
    },
    setOptionValue(item, val) {
      item.value = isNumberStr(val) ? +val : val
    },
    setDefaultValue(val) {
      if (Array.isArray(val)) {
        return val.join(',')
      }
      if (['string', 'number'].indexOf(val) > -1) {
        return val
      }
      if (typeof val === 'boolean') {
        return `${val}`
      }
      return val
    },
    onDefaultValueInput(str) {
      if (isArray(this.activeData.defaultValue)) {
        // 数组
        this.$set(
          this.activeData,
          'defaultValue',
          str.split(',').map(val => (isNumberStr(val) ? +val : val))
        )
      } else if (['true', 'false'].indexOf(str) > -1) {
        // 布尔
        this.$set(this.activeData, 'defaultValue', JSON.parse(str))
      } else {
        // 字符串和数字
        this.$set(
          this.activeData,
          'defaultValue',
          isNumberStr(str) ? +str : str
        )
      }
    },
    onSwitchValueInput(val, name) {
      if (['true', 'false'].indexOf(val) > -1) {
        this.$set(this.activeData, name, JSON.parse(val))
      } else {
        this.$set(this.activeData, name, isNumberStr(val) ? +val : val)
      }
    },
    setTimeValue(val, type) {
      const valueFormat = type === 'week' ? dateTimeFormat.date : val
      this.$set(this.activeData, 'defaultValue', null)
      this.$set(this.activeData, 'value-format', valueFormat)
      this.$set(this.activeData, 'format', val)
    },
    spanChange(val) {
      this.formConf.span = val
    },
    multipleChange(val) {
      this.$set(this.activeData, 'defaultValue', val ? [] : '')
    },
    dateTypeChange(val) {
      this.setTimeValue(val === 'week' ? this.$t('tool.yyyy 第 WW 周') : dateTimeFormat[val], val)
    },
    rangeChange(val) {
      this.$set(
        this.activeData,
        'defaultValue',
        val ? [this.activeData.min, this.activeData.max] : this.activeData.min
      )
    },
    rateTextChange(val) {
      if (val) this.activeData['show-score'] = false
    },
    rateScoreChange(val) {
      if (val) this.activeData['show-text'] = false
    },
    colorFormatChange(val) {
      this.activeData.defaultValue = null
      this.activeData['show-alpha'] = val.indexOf('a') > -1
      this.activeData.renderKey = +new Date() // 更新renderKey,重新渲染该组件
    },
    openIconsDialog(model) {
      this.iconsVisible = true
      this.currentIconModel = model
    },
    setIcon(val) {
      this.activeData[this.currentIconModel] = val
    },
    tagChange(tagIcon) {
      let target = inputComponents.find(item => item.tagIcon === tagIcon)
      if (!target) target = selectComponents.find(item => item.tagIcon === tagIcon)
      this.$emit('tag-change', target)
    }
  }
}
</script>

<style lang="scss" scoped>
.right-board {
  width: 350px;
  position: absolute;
  right: 0;
  top: 0;
  padding-top: 3px;
  .field-box {
    position: relative;
    height: calc(100vh - 42px);
    box-sizing: border-box;
    overflow: hidden;
  }
  .el-scrollbar {
    height: 100%;
  }
}
.select-item {
  display: flex;
  border: 1px dashed #fff;
  box-sizing: border-box;
  & .close-btn {
    cursor: pointer;
    color: #f56c6c;
  }
  & .el-input + .el-input {
    margin-left: 4px;
  }
}
.select-item + .select-item {
  margin-top: 4px;
}
.select-item.sortable-chosen {
  border: 1px dashed #409eff;
}
.select-line-icon {
  line-height: 32px;
  font-size: 22px;
  padding: 0 4px;
  color: #777;
}
.option-drag {
  cursor: move;
}
.time-range {
  .el-date-editor {
    width: 227px;
  }
  ::v-deep .el-icon-time {
    display: none;
  }
}
.document-link {
  position: absolute;
  display: block;
  width: 26px;
  height: 26px;
  top: 0;
  left: 0;
  cursor: pointer;
  background: #409eff;
  z-index: 1;
  border-radius: 0 0 6px 0;
  text-align: center;
  line-height: 26px;
  color: #fff;
  font-size: 18px;
}
.node-label{
  font-size: 14px;
}
.node-icon{
  color: #bebfc3;
}
</style>
