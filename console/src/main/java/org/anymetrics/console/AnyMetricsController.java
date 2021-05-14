package org.anymetrics.console;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.anymetrics.console.base.MR;
import org.anymetrics.console.base.ResultPage;
import org.anymetrics.core.task.ConfigTaskManage;
import org.anymetrics.core.task.PipelineTask;
import org.anymetrics.core.task.PipelineTaskContext;
import org.anymetrics.core.task.PipelineTaskManage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.UUID;


@RequestMapping("/rest/anyMetrics")
@RestController
public class AnyMetricsController  {

    @RequestMapping(value = "list")
    public Object list() {
        ResultPage resultPage = new ResultPage();

        String configsStr = ConfigTaskManage.getConfigsStr();
        if(configsStr != null) {
            JSONArray configs = JSON.parseArray(configsStr);
            for(int i = 0; i < configs.size(); i ++) {
                JSONObject jsonObject = configs.getJSONObject(i);
                String taskId = jsonObject.getString("id");
                int status = 0;//stoped
                PipelineTask task = PipelineTaskManage.getTask(taskId);
                if(task != null) {
                    status = task.getStatus();
                }
                jsonObject.put("status", status);
            }
            resultPage.setItems(configs);
        }
        return MR.ok().setResultPage(resultPage);
    }


    /**
     * 添加任务
     * @param newTaskJsonConfig
     * @return
     */
    @RequestMapping(value = "/addTask")
    public Object addTask(String newTaskJsonConfig) {
        try {
            JSONObject jsonObject = addTaskConfig(newTaskJsonConfig);
            PipelineTaskManage.loadNewTask(jsonObject.toJSONString(), false);
        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }


    /**
     * 添加并启动任务
     * @param newTaskJsonConfig
     * @return
     */
    @RequestMapping(value = "/addAndStartTask")
    public Object addAndStartTask(String newTaskJsonConfig) {
        try {
            JSONObject jsonObject = addTaskConfig(newTaskJsonConfig);

            PipelineTaskManage.loadNewTask(jsonObject.toJSONString(), true);
        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }


    /**
     * 修改任务
     * @return
     */
    @RequestMapping(value = "/updateTask")
    public Object updateTask(String newTaskJsonConfig) {
        try {

            updateTaskConfig(newTaskJsonConfig);

        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }

    /**
     * 修改并重启任务
     * @param newTaskJsonConfig
     * @return
     */
    @RequestMapping(value = "/updateAndStartTask")
    public Object updateAndStartTask(String newTaskJsonConfig) {
        try {

            updateTaskConfig(newTaskJsonConfig);

            PipelineTaskManage.reloadTask(newTaskJsonConfig, true);

        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }

    /**
     * 删除任务
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/deleteTask")
    public Object deleteTask(String taskId) {
        try {

            deleteTaskConfig(taskId);

            PipelineTaskManage.removeTask(taskId);

        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }



    private void deleteTaskConfig(String taskId) {

        String configsStr = ConfigTaskManage.getConfigsStr();
        JSONArray jsonArray = JSONArray.parseArray(configsStr);
        Iterator<Object> iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            if(next.getString("id").equals(taskId)) {
                //remove
                iterator.remove();
            }
        }

        if(!ConfigTaskManage.updateConfigStr(jsonArray.toJSONString())) {
            throw new IllegalArgumentException("删除任务失败");
        }
    }


    private void updateTaskConfig(String newTaskJsonConfig) {
        JSONObject newTask = JSONObject.parseObject(newTaskJsonConfig);

        String configsStr = ConfigTaskManage.getConfigsStr();
        JSONArray jsonArray = JSONArray.parseArray(configsStr);
        Iterator<Object> iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            if(next.getString("id").equals(newTask.getString("id"))) {
                //remove
                iterator.remove();
            }
        }
        //add
        jsonArray.add(newTask);

        if(!ConfigTaskManage.updateConfigStr(jsonArray.toJSONString())) {
            throw new IllegalArgumentException("修改任务失败");
        }
    }

    private JSONObject addTaskConfig(String newTaskJsonConfig) {
        JSONObject newTask = JSONObject.parseObject(newTaskJsonConfig);
        newTask.put("id", UUID.randomUUID().toString());

        JSONArray jsonArray;
        String configsStr = ConfigTaskManage.getConfigsStr();
        if(configsStr == null) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = JSONArray.parseArray(configsStr);
        }
        //add
        jsonArray.add(newTask);

        if(!ConfigTaskManage.updateConfigStr(jsonArray.toJSONString())) {
            throw new IllegalArgumentException("新增任务失败");
        }
        return newTask;
    }

    /**
     * 重启任务
     * @param newTaskJsonConfig
     * @return
     */
    @RequestMapping(value = "/reloadTask")
    public Object reloadTask(String newTaskJsonConfig) {
        try {
            PipelineTaskManage.reloadTask(newTaskJsonConfig, true);
        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }

    /**
     * 启动任务
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/startTask")
    public Object startTask(String taskId) {
        try {
            PipelineTask task = PipelineTaskManage.getTask(taskId);
            task.start();
        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }


    /**
     * 停止任务
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/stopTask")
    public Object stopTask(String taskId) {
        try {
            PipelineTask task = PipelineTaskManage.getTask(taskId);
            task.stop();
        } catch(Exception e) {
            e.printStackTrace();
            return MR.error(e.getMessage());
        }
        return MR.ok();
    }

    /**
     * 获取任务日志
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/getTaskState")
    public Object getTaskState(String taskId) {
        PipelineTaskContext context = PipelineTaskContext.getTaskContext(taskId);
        if(context == null) {
            return MR.error("任务未运行");
        }
        return MR.ok().setEntity(context.getLog());
    }

}
