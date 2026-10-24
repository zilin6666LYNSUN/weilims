<template>
  <div class="app-container">
    <el-row>
        <el-col :span="12">
            <el-card class="box-card">
                <div slot="header" class="clearfix">
                    <span>{{ this.$t('mes.班组') }}</span>
                    <el-button style="float: right; padding: 3px 0" v-if="optType !='view'" @click="handleAdd" type="text">{{ this.$t('lims.添加') }}</el-button>
                </div>
                <el-table v-loading="loading" :data="teamList" @row-click="onRowClick">
                    <el-table-column :label="$t('mes.班组编号')" align="center" prop="teamCode" >
                    </el-table-column>
                    <el-table-column :label="$t('mes.班组名称')" align="center" prop="teamName" />
                    <el-table-column :label="$t('common.operation')" align="center" v-if="optType !='view'" class-name="small-padding fixed-width">
                        <template slot-scope="scope">
                        <el-button
                            size="mini"
                            type="text"
                            icon="el-icon-delete"
                            @click="handleDelete(scope.row)"
                            v-hasPermi="['mes:cal:calplan:edit']"
                        >{{ $t('common.delete') }}</el-button>
                        </template>
                    </el-table-column>                    
                </el-table>           
                <TeamSelect ref="teamSelect" @onSelected="onTeamSelected"></TeamSelect>    
            </el-card>
        </el-col>
        <el-col :span="12">
            <el-card class="box-card">
                <div slot="header" class="clearfix">
                    <span>{{ this.$t('mes.成员') }}</span>                    
                </div>
                <el-table v-loading="loading" :data="teammemberList">
                    <el-table-column :label="$t('mes.用户名')" align="center" prop="userName" />
                    <el-table-column :label="$t('system.用户昵称')" align="center" prop="nickName" />
                    <el-table-column :label="$t('lims.电话')" align="center" prop="tel" />                    
                </el-table>
            </el-card>
        </el-col>
    </el-row>
  </div>
</template>

<script>
import { listPlanteam,  delPlanteam, addPlanteam } from "@/api/mes/cal/planteam";
import { listTeammember } from "@/api/mes/cal/teammember";
import TeamSelect from "@/components/calTeamSelect/multi.vue"
export default {
    name:"PlanTeam",
    components:{TeamSelect},
    props:{
        planId:null,
        optType:null,
        calendarType:null
    },
    data(){
        return {
            // 遮罩层
            loading: true,
            //班组表格数据
            teamList:[],
            // 班组成员表格数据
            teammemberList: [],
            form:{},
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                planId: this.planId,
                teamId: null,
                teamCode: null,
                teamName: null,
                calendarType:this.calendarType
            },
        }
    },
    created() {
        this.getTeamList();     
    },
    methods:{
        getTeamList(){
            this.loading = true;
            listPlanteam(this.queryParams).then(response => {
                this.teamList = response.rows;                
                this.loading = false;
            });
        },
        getMemberList(id){
            var param = {
                teamId:id
            }
            this.loading = true;
            listTeammember(param).then(response => {
                this.teammemberList = response.rows;
                this.loading = false;
            });
        },
        onRowClick(row){
            this.getMemberList(row.teamId);
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.$refs.teamSelect.showFlag = true;
        },
        onTeamSelected(rows){
            rows.forEach(team => {
                this.form.teamId = team.teamId;
                this.form.teamCode = team.teamCode;
                this.form.teamName = team.teamName;
                this.form.planId = this.planId;
                this.form.calendarType = this.calendarType;
                addPlanteam(this.form).then(response => {
                    this.getTeamList();                    
                });
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const recordIds = row.recordId
            this.$modal.confirm(this.$t('mes.是否确认删除班组？')).then(function() {
                return delPlanteam(recordIds);
            }).then(() => {
                this.getTeamList();
                this.$modal.msgSuccess(this.$t('system.删除成功'));
            }).catch(() => {});
        },
    }
}
</script>

<style>

</style>