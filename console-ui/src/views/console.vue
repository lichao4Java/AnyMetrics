<template>

  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">

        <el-form-item>
          <el-button size="mini" type="primary" @click="handleFilter" icon="el-icon-search">刷新</el-button>
          <el-button size="mini" type="primary"  icon="el-icon-plus" @click="handlePreCreateTask">添加任务</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-tabs tabPosition="left" type="border-card" v-model="tab1" style="width: 100%;">

        <el-tab-pane
        :key="item.id"
        v-for="(item,index) in list"
        :label="item.name"
        :name="item.index">
        
          <span slot="label">
            <span v-if="item.status == 0" style="color:#c8c6c6">[Stoped]</span>
            <span v-if="item.status == 1" style="color:#0fca0f">[Running]</span>
            <span v-if="item.status == 2" style="color:#ff3f3f">[Error]</span> 
            {{item.name}}
          </span>
        
         <el-button-group >
            <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleStartTask(item.id)" style="margin-left:5px">Start</el-button>
            <el-button size="mini" type="warning" icon="el-icon-switch-button" @click="handleStopTask(item.id)" style="margin-left:5px">Stop</el-button>

          <el-popconfirm
            confirm-button-text='好的'
            cancel-button-text='不用了'
            icon="el-icon-info"
            icon-color="red"
            title="确定要删除吗？"
            @onConfirm="handleDeleteTask(item.id)"
          >
            <el-button size="mini" type="danger" icon="el-icon-delete" slot="reference" style="margin-left:5px">删除</el-button>
          </el-popconfirm>

            <el-button size="mini" type="primary" icon="el-icon-edit" @click="handlePreUpdateTask(item)" style="margin-left:5px">修改任务</el-button>
          </el-button-group>

    <div style="margin-top: 20px;"></div>

    <el-tabs type="border-card" v-model="tab2" style="width: 100%" @tab-click="handleSubTabClick">

       <el-tab-pane label="info" :name="item.index">
          <span slot="label"><i class="el-icon-info"></i> info</span>

          <el-form label-position="left" label-width="100px"  size="small">
                <el-form-item label="任务ID">
                  <span>{{ item.id }}</span>
                </el-form-item>
                <el-form-item label="任务名称">
                  <span>{{item.name }}</span>
                </el-form-item>
                <el-form-item label="数据源">
                  <json-viewer
                :value="item.dataSource"
                preview-mode></json-viewer>
                </el-form-item>
                <el-form-item label="收集规则">
                  <json-viewer
                  :value="item.rule"
                  preview-mode></json-viewer>
                </el-form-item>
                <el-form-item label="收集器">
                  <json-viewer
                :value="item.collector"
                preview-mode></json-viewer>
                </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane :value="item.id" label="logs" :name="item.index">
          <span slot="label"><i class="el-icon-s-comment"></i> logs</span>

          <div style="background-color: rgb(22, 23, 25);color: rgb(218, 217, 218);font-size: 13px;font-family: Menlo, Monaco, Consolas, Courier New, monospace;line-height: 1.5">
            <div v-for="log in logs" style="overflow-wrap: break-word;word-break: break-all;">
            {{log}}
            </div>
          </div>
          <div style="margin-top: 20px;"></div>
          <div>
            <el-button @click="getLogs(item.id)"  type="success" icon="el-icon-check">刷新</el-button>
          </div>
            
        </el-tab-pane>
        
         <el-tab-pane lazy label="iframe" :name="item.index">
               <span slot="label"><i class="el-icon-files"></i> iframe</span>
              <iframe :src="item.iframe" width="100%" height="1000px" />
        </el-tab-pane>
      </el-tabs>
    </el-tab-pane>
</el-tabs>


<el-dialog title="配置任务" :visible.sync="settingDialog">

  <el-form label-position="right" label-width="100px">

        <el-form-item label="名称" prop="name">
          <el-input type="text" v-model="temp.name"></el-input>
        </el-form-item>

        <el-form-item label="任务类型" prop="kind">
          <el-select v-model="tempExt.ruleKind" @change="ruleKindChange">
            <el-option
              v-for="item in ruleKindTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              <span style="float: left">{{ item.label }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <div v-if="tempExt.timeWindowVisiable">
            <el-form-item label="时间窗口" prop="timeWindow">
              <el-input type="text" v-model="tempExt.timeWindow" placeholder="秒"></el-input>
            </el-form-item>
            <el-form-item label="数据源类型" prop="type">
              <el-select v-model="tempExt.dataSourceType" @change="dataSourceChange">
                <el-option
                  v-for="item in streamDataSourceTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                  <span style="float: left">{{ item.label }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
                </el-option>
              </el-select>
            </el-form-item>
        </div>

      <div v-if="tempExt.scheduleVisiable">
        <el-form-item label="调度间隔" prop="schedule" v-if="tempExt.scheduleVisiable">
          <el-input type="text" v-model="tempExt.schedule" placeholder="秒"></el-input>
        </el-form-item>

        <el-form-item label="数据源类型" prop="type">
              <el-select v-model="tempExt.dataSourceType" @change="dataSourceChange">
                <el-option
                  v-for="item in scheduleDataSourceTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                  <span style="float: left">{{ item.label }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
                </el-option>
              </el-select>
            </el-form-item>
      </div>

        <el-form-item label="数据源" prop="dataSource">
          <el-input type="textarea" v-model="temp.dataSource" rows="8" style="background-color:black;color:white"></el-input>
        </el-form-item>
        
         <el-form-item label="收集规则" prop="rule">
          <el-input type="textarea" v-model="temp.rule" rows="15"></el-input>
        </el-form-item>

        <el-form-item label="收集器类型" prop="type">
              <el-select v-model="tempExt.collectorType" @change="collectorChange">
                <el-option
                  v-for="item in collectorTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                  <span style="float: left">{{ item.label }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
                </el-option>
              </el-select>
            </el-form-item>

         <el-form-item label="收集器" prop="collector">
          <el-input type="textarea" v-model="temp.collector" rows="15"></el-input>
        </el-form-item>

        <el-form-item label="iframe" prop="rule">
          <el-input type="text" v-model="temp.iframe" placeholder="控制台中可内嵌iframe"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="settingDialog = false">取消</el-button>
        <el-button v-if="dialogStatus=='新增'" type="primary" @click="handleAddTask">新增</el-button>
        <el-button v-if="dialogStatus=='新增'" type="primary" @click="handleAddAndStartTask">新增并启动任务</el-button>
        <el-button v-if="dialogStatus=='修改'" type="primary" @click="handleUpdateTask">修改</el-button>
        <el-button v-if="dialogStatus=='修改'" type="primary" @click="handleUpdateAndStartTask">修改并重启任务</el-button>

      </div>
</el-dialog>
  </div>
</template>

<style>
.el-form-item {
    margin-bottom: 5px !important;
}
.el-tabs__item{
    text-align:left !important;
}
.el-form-item__label {
   font-size:16px;
   text-align: right !important;
 }
.el-form-item__content{
  color:#808080;
}
.el-textarea textarea {
  background-color: #000000;
  color: #ffffff;
}
.el-form-item {
    margin-bottom: 15px !important;
}
.filter-container {
    padding-bottom: 0px;
}
</style>

<script>
import { getAnyMetricsList, getAnyMetricsTaskState, startTask, stopTask, deleteTask, addTask, addAndStartTask, updateTask, updateAndStartTask} from '../api/console'

export default {
  data() {
    return {
      tab1: '0',
      tab2: '0',
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        pageNum: 1,
        numPerPage: 20
      },
      temp: {
        id :'',
        name :'',
        dataSource : '',
        rule :'',
        collector:'',
        iframe :''
      },
      tempExt : {
        type:'',
        dataSourceType : '',
        collectorType : '',
        timeWindowVisiable : false,
        scheduleVisiable : false,
        schedule:30,
        timeWindow:30
      },
      settingDialog: false,
      dialogStatus : '',
      logs :[],
      logId : '',
      rules: {
      },
      ruleKindTypes :[{value : 'schedule', label :'有界数据'},{value : 'stream', label :'无界数据'}],
      streamDataSourceTypes :[{value : 'kafka', label :'kafka'}],
      scheduleDataSourceTypes :[{value : 'mysql', label :'mysql'}, {value : 'simpleHTTP', label :'simpleHTTP'}],
      collectorTypes :[{value : 'prometheus', label :'prometheus'}],
      dataSourceConfigTemp : {
        mysql: `
{
    "type":"mysql",
    "jdbcurl":"jdbc:mysql://ip:port/database",
    "sql":"",
    "username":"",
    "password":""
}`,
        kafka : `
{
    "type":"kafka",
    "kafkaAddress":"ip:port",
    "topic":"topic_name",
    "groupId":"group_id"
}`,
        simpleHTTP :  `
{
    "type":"simpleHTTP",
    "url":""
}`
      },
      ruleConfigTemp : {
        stream : `
{
    "kind":"stream",
    "timeWindow":$timeWindow,
    "filters":[
        {
            "expression":"(.*)",
            "type":"regular"
        },
        {
            "expression":"#$1 == #$1",
            "type":"el"
        },
    ]
}`,
schedule : `
{
    "kind":"schedule",
    "interval":$schedule,
    "filters":[
        {
            "expression":"(.*)",
            "type":"regular"
        },
        {
            "expression":"#$1 == #$1",
            "type":"el"
        },
    ]
}
`
      }
,
      collectorConfigTemp : {
         prometheus: `{
    "type":"prometheus",
    "pushGateway":"ip:port",
    "job":"job_name",
    "metrics":[
        {
            "name":"metrics_name",
            "help":"metrics_help",
"labelNames":[
                "lable_name"
            ],
            "labels":[
                "lable_value"
            ],
            "value":"1",
            "type":"gauge|counter|histogram"
        }
    ]
}`
      }
    }
  },
  filters:{
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getAnyMetricsList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.numPerPage = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    resetTemp() {

      this.temp = {
          name :'',
          dataSource : '',
          rule :'',
          collector:'',
          iframe :''
        }
      this.tempExt = {
          type:'',
          dataSourceType : '',
          collectorType : '',
          timeWindowVisiable : false,
          scheduleVisiable : false,
          schedule:30,
          timeWindow:30
      }
      

    },
    getLogs(id) {
      this.logId = id
      getAnyMetricsTaskState({'taskId':id}).then(response => {
        this.logs = response.data.entity.logs
      })
    },
    handleStartTask(id) {
      startTask({'taskId' :id}).then(response => {
        this.$notify({
            title: '成功',
            message: '启动成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleStopTask(id) {
      stopTask({'taskId' :id}).then(response => {
        this.$notify({
            title: '成功',
            message: '停止成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleDeleteTask(id) {
      deleteTask({'taskId' :id}).then(response => {
        this.getList()
        this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleSubTabClick(tab) {
      if(tab.index == 1) {
        this.logs = []
        this.getLogs(tab.$attrs.value)
      }
    },
    handlePreUpdateTask(row) {
      this.resetTemp()
      this.dialogStatus = '修改'
      this.settingDialog = true
      this.temp.id = row.id
      this.tempExt.ruleKind = row.rule.kind
      this.tempExt.dataSourceType = row.dataSource.type
      this.tempExt.collectorType = row.collector.type
      if(row.rule.kind == 'schedule') {
        this.tempExt.scheduleVisiable = true
        this.tempExt.schedule = row.rule.interval
      } 
      else if(row.rule.kind == 'stream') {
        this.tempExt.timeWindowVisiable = true
        this.tempExt.timeWindow = row.rule.timeWindow
      }
      this.temp.name = row.name
      this.temp.dataSource = JSON.stringify(row.dataSource, null, 4)
      this.temp.rule = JSON.stringify(row.rule, null, 4)
      this.temp.collector = JSON.stringify(row.collector, null, 4)
      this.temp.iframe = row.iframe
    },
    handlePreCreateTask() {
      this.dialogStatus = '新增'
      this.settingDialog = true
      this.resetTemp()
    },
    getTempJSON() {
      return JSON.stringify({
        id :this.temp.id,
        name : this.temp.name,
        dataSource : JSON.parse(this.temp.dataSource),
        rule :JSON.parse(this.temp.rule),
        collector: JSON.parse(this.temp.collector),
        iframe : this.temp.iframe
      })
    },
    handleAddTask(){

      addTask({'newTaskJsonConfig' : this.getTempJSON()}).then(response => {
        this.getList()
        this.settingDialog = false
        this.$notify({
            title: '成功',
            message: '添加成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleAddAndStartTask(){
      addAndStartTask({'newTaskJsonConfig' : this.getTempJSON()}()).then(response => {
        this.getList()
        this.settingDialog = false
        this.$notify({
            title: '成功',
            message: '添加并启动成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleUpdateTask(){
      updateTask({'newTaskJsonConfig' : this.getTempJSON()}).then(response => {
        this.getList()
        this.settingDialog = false
        this.$notify({
            title: '成功',
            message: '修改成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    handleUpdateAndStartTask(){
      updateAndStartTask({'newTaskJsonConfig' : this.getTempJSON()}).then(response => {
        this.getList()
        this.settingDialog = false
        this.$notify({
            title: '成功',
            message: '修改并启动成功',
            type: 'success',
            duration: 5000
          })
      })
    },
    ruleKindChange(sel) {
      if(sel == 'schedule') {
        this.tempExt.dataSourceType = ''
        this.temp.dataSource = ''
        this.tempExt.scheduleVisiable = true
        this.tempExt.timeWindowVisiable = false
      } 
      else if(sel == 'stream') {
        this.tempExt.dataSourceType = ''
        this.temp.dataSource = ''
        this.tempExt.scheduleVisiable = false
        this.tempExt.timeWindowVisiable = true
      }
    },
    dataSourceChange(sel) {

      this.temp.dataSource = this.dataSourceConfigTemp[this.tempExt.dataSourceType]
      this.temp.rule = this.ruleConfigTemp[this.tempExt.ruleKind].replace('$schedule', this.tempExt.schedule || 30).replace('$timeWindow', this.tempExt.timeWindow || 30)

    },
    collectorChange(sel) {
      this.temp.collector = this.collectorConfigTemp[sel]
      }
  }
}
</script>
