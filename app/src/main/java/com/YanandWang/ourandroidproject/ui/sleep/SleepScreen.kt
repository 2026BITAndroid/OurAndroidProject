package com.YanandWang.ourandroidproject.ui.sleep
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter

@Composable
fun SleepScreen() {
    val vm: SleepViewModel = viewModel()
    // 修正：变量名是records
    val records: List<SleepRecord> by vm.records.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("睡了么 - 自动统计睡眠", modifier = Modifier.padding(bottom = 20.dp))
        Text("规则：凌晨4点前最后息屏=入睡；4点后首次亮屏=起床")
        Spacer(modifier = Modifier.height(24.dp))
        Text("历史睡眠记录：")

        if(records.isEmpty()){
            Text("暂无睡眠记录，夜间锁屏息屏、次日亮屏后自动生成记录")
        }else{
            records.forEach { item: SleepRecord ->
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text("入睡：${item.sleepTime.format(formatter)}")
                    Text("起床：${item.wakeTime?.format(formatter)}")
                    Text("睡眠时长：${"%.1f".format(item.sleepHour)} h")
                    Text("睡眠建议：${item.suggest}")
                }
            }
        }
    }
}