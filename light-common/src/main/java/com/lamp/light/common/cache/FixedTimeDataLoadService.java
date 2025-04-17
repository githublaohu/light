package com.lamp.light.common.cache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.lamp.light.api.cache.DataLoadConfigData;
import com.lamp.light.api.data.channel.model.DataLoadRequest;
import com.lamp.light.api.cache.DataLoadService;
import com.lamp.light.api.cache.DataOperate;
import com.lamp.light.api.cache.IncrementLoadResult;

public class FixedTimeDataLoadService {

    private List<DataLoadAndHandlerRelationship> dataLoadAndHandlerRelationshipList;

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(4);


    public void register(DataLoadService<Object> dataLoadService){

    }

    public void register(DataLoadService<Object> dataLoadService, DataLoadConfigData dataLoadConfigData){

    }

    public void registerMonitor(DataOperate<Object, Object> dataOperate){

    }


    /**
     * 注册对应表明这个对象需要注册
     * @param dataOperate
     * @return
     * @param <T>
     */
    public <T>T register(Class<?> dataOperate){
        return (T)null;
    }

    public void register(LoadingPlan loadingPlan){

    }



    static class DataLoadAndHandlerRelationship{

        private DataLoadService<Object> dataLoadService;

        private List<DataOperate<Object,Object>> dataOperate;

        private DataLoadConfigData dataLoadConfig;

    }

    static class LoadingPlan{

        private LocalDateTime startTime;

        private LocalDateTime endTime;


        private DataLoadAndHandlerRelationship dataLoadAndHandlerRelationship;

        private DataLoadService<Object> dataLoadService;

        private List<DataOperate<Object,Object>> dataOperate;

        private List<Object> data;

        private LocalDateTime nextPlanTime;

        private DataLoadRequest dataLoadRequest = new DataLoadRequest();

        private long fullLoad

        private IncrementLoadResult<Object> incrementLoadResult;

        void fullLoadComplete(List<Object> data,Throwable e){
            if(Objects.nonNull(e)){

            }
            if(Objects.isNull(data) || data.isEmpty()){

            }
            this.data = data;
            dataOperate.forEach( (objectObjectDataOperate) ->{
                try {
                    objectObjectDataOperate.fullUpdate(data);
                }catch (Exception ee){

                }
            });
            // 下一次 加载
        }

        void incrementLoadComplete(IncrementLoadResult<Object> incrementLoadResult , Throwable e){

            if(!incrementLoadResult.isIncrement()){
                this.fullLoadComplete(incrementLoadResult.getInsert(),e);
                return;
            }

            if(Objects.nonNull(e)){

            }
            if(Objects.isNull(incrementLoadResult)){

            }



            dataOperate.forEach( (objectObjectDataOperate) ->{

                try {
                    if(Objects.isNull(incrementLoadResult.getDelete()) || incrementLoadResult.getDelete().isEmpty()){

                    }
                    objectObjectDataOperate.delete(incrementLoadResult.getDelete());
                    if(Objects.isNull(incrementLoadResult.getUpdate()) || incrementLoadResult.getUpdate().isEmpty()){

                    }
                    objectObjectDataOperate.delete(incrementLoadResult.getUpdate());
                    if(Objects.isNull(incrementLoadResult.getInsert()) || incrementLoadResult.getInsert().isEmpty()){

                    }
                    objectObjectDataOperate.delete(incrementLoadResult.getInsert());
                }catch (Exception ee){

                }
            });
        }

        public void run(){
            try{
                dataLoadService.fullLoad(dataLoadRequest).whenCompleteAsync( this::fullLoadComplete);
                dataLoadService.incrementLoad(dataLoadRequest).whenCompleteAsync(this::incrementLoadComplete);
            }catch (Exception e){
            }
        }

    }
}
