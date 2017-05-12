package edf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Scheduler {

    static ScheduledTasks schedule(final List<Task> taskList) {

        int lcm = calcLCM(taskList);
        ScheduledTasks out = new ScheduledTasks();
        Map<Integer, List<Task>> waitingMap = new HashMap<>();


        for(int timeUnit = 0; timeUnit < lcm; timeUnit++) {

            out.getDeadlinesList().add(timeUnit, new ArrayList<>());

            //add iterative tasks into the waiting list
            for(Task t : taskList)
                if(timeUnit % t.getPeriod() == 0) {

                    if(! waitingMap.containsKey(timeUnit + t.getPeriod()))
                        waitingMap.put(timeUnit + t.getPeriod(), new ArrayList<>());

                    for(int i = 0; i < t.getET(); i++)
                        waitingMap.get(timeUnit + t.getPeriod()).add(t);

                    out.getDeadlinesList().get(timeUnit).add(t);
                }

            if(! waitingMap.isEmpty()) {
                //the highest priority task has the minimum period
                Integer minKey = waitingMap.keySet().stream().min(Integer::compareTo).get();
                out.getTaskList().add(waitingMap.get(minKey).get(0));
                waitingMap.get(minKey).remove(0);
                if(waitingMap.get(minKey).isEmpty())
                    waitingMap.remove(minKey);
            } else
                out.getTaskList().add(null);

        }

        out.getDeadlinesList().remove(0);
        out.getDeadlinesList().add(new ArrayList<>());
        return out;
    }


    static int calcLCM(List<Task> taskList) {

        int lcm = taskList.get(0).getPeriod();
        for(boolean flag = true; flag; ) {
            for(Task x : taskList) {
                if(lcm % x.getPeriod() != 0) {
                    flag = true;
                    break;
                }
                flag = false;
            }
            lcm = flag? (lcm + 1) : lcm;
        }

        return lcm;
    }


    static class ScheduledTasks {

        private List<Task> taskList = new ArrayList<>();
        private List<List<Task>> deadlinesList = new ArrayList<>();

        public List<Task> getTaskList() {
            return taskList;
        }

        public List<List<Task>> getDeadlinesList() {
            return deadlinesList;
        }
    }

}
