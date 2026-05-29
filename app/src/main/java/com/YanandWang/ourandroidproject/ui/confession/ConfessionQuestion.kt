package com.YanandWang.ourandroidproject.ui.confession

data class ConfessionQuestion(
    val question: String,
    val options: List<String>
)

object ConfessionBank {
    val questions = listOf(
        // 生活习惯类（诙谐口语版）
        ConfessionQuestion(
            "今天熬大夜了吗？",
            listOf("早睡达人，准时躺平", "稍微晚睡了一小会儿", "通宵，悔不当初！")
        ),
        ConfessionQuestion(
            "三餐有没有好好吃，还是随便糊弄一下？",
            listOf("必须按时干饭", "饿了才想起来吃饭", "直接跳过，啥也没吃")
        ),
        ConfessionQuestion(
            "今天有没有瘫着不动，化身“沙发/床绑定选手”？",
            listOf("经常起身溜达", "坐久了才动一动", "从早瘫到晚，半步不想走")
        ),
        ConfessionQuestion(
            "东西用完随手丢，桌面是不是乱成一团啦？",
            listOf("收拾得整整齐齐", "乱了一点点而已", "乱到自己都看不下去")
        ),
        ConfessionQuestion(
            "手机是不是黏在手上，刷了好久停不下来？",
            listOf("合理美美把玩，适可而止", "不知不觉玩超了", "沉迷其中，根本放不下")
        ),
        ConfessionQuestion(
            "早上赖床了吗？",
            listOf("闻鸡起舞，绝不拖沓", "眯了几分钟才起床", "被子最终还是没有放开我")
        ),
        ConfessionQuestion(
            "今天喝水够不够，不会又忘了补水吧？",
            listOf("吨吨吨，喝水超积极", "喝了没几杯", "已经干瘪…")
        ),
        ConfessionQuestion(
            "零食、奶茶、油炸小吃是不是炫太多了？",
            listOf("管住嘴，不吃就不吃", "浅尝几口解解馋", "敞开吃，快乐至上")
        ),
        ConfessionQuestion(
            "离开房间后，有没有忘记关灯断电？",
            listOf("次次检查，细心本人", "偶尔粗心忘啦", "根本没想起来过")
        ),
        ConfessionQuestion(
            "今天有没有动一动，简单活动下身体？",
            listOf("专门运动了一会儿", "起身随便晃了晃", "人体工学椅重度依赖")
        ),
        ConfessionQuestion(
            "起床洗漱是不是草草了事，主打一个敷衍？",
            listOf("认真打理，干干净净", "简单洗两把完事", "直接摆烂")
        ),
        ConfessionQuestion(
            "吃饭的时候，是不是一边干饭一边刷视频？",
            listOf("专心干饭是对美食最大的尊重", "偶尔边吃边看", "吃饭必刷，已成习惯")
        ),
        ConfessionQuestion(
            "睡前还抱着手机刷，迟迟不愿进入梦乡？",
            listOf("到点就放下手机睡觉", "玩一小会儿就休息", "刷着刷着听到了鸟叫")
        ),
        ConfessionQuestion(
            "垃圾是不是堆了好久，才想起来倒掉？",
            listOf("随有随清，干干净净", "攒一点再一起倒", "堆积如山，随缘去倒")
        ),
        ConfessionQuestion(
            "天天点外卖凑活？",
            listOf("自己动手丰衣足食", "偶尔点外卖解馋", "三餐全靠外卖续命")
        ),

        // 少量自省类
        ConfessionQuestion(
            "遇到不顺心的事，会不会忍不住闹脾气？",
            listOf("心态稳稳的，没啥情绪", "心里小小郁闷一下", "当场就忍不住吐槽发火")
        ),
        ConfessionQuestion(
            "立下的小flag，今天有没有坚持完成？",
            listOf("完美打卡，说到做到", "完成了一部分", "flag倒地，彻底摆烂")
        ),
        ConfessionQuestion(
            "别人说话的时候，有没有认真听人家讲？",
            listOf("专心倾听，积极回应", "偶尔走神放空", "总想插嘴，没耐心听")
        ),
        ConfessionQuestion(
            "做错事之后，会不会反思自己的小问题？",
            listOf("知错就改，及时反省", "过后才慢慢琢磨", "无所谓，下次还犯")
        ),
        ConfessionQuestion(
            "面对身边的人，有没有保持友善的态度？",
            listOf("和和气气相处愉快", "态度平平淡淡", "懒得理ta，专注自我")
        )
    )
}