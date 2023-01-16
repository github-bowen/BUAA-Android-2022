package com.example.success.ui.home;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.success.AlarmReceiver;
import com.example.success.ChineseMemory;
import com.example.success.ChineseTest;
import com.example.success.EnglishMemory;
import com.example.success.EnglishTest;
import com.example.success.MainActivity;
import com.example.success.MainViewModel;
import com.example.success.R;
import com.example.success.ShowTask;
import com.example.success.databinding.FragmentHomeBinding;
import com.example.success.view.CurveView;

import java.util.Calendar;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    // 提醒管理
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar mCalendar;
    private FragmentHomeBinding binding;
    protected static final int SET_WORD = 0;
    protected static final int SET_KNOWLEDGE = 1;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        final TextView textView_name = binding.textViewName;
//        final TextView textView_passwd = binding.textViewPasswd;
//        String name = mainViewModel.getName().getValue();
//        textView_name.setText(name);
//        textView_passwd.setText(mainViewModel.getPasswd().getValue());
        Button testButton = binding.test;
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int englishNum = mainViewModel.getWordTestSum().getValue();
                int chineseNum = mainViewModel.getKnowledgeTestSum().getValue();
                if (englishNum != 0) {
                    //Bundle bundle = new Bundle();
                    // bundle放入单词的信息
                    Intent callTest = new Intent(getActivity(), EnglishTest.class);
                    //callTest.putExtras(bundle);
                    startActivity(callTest);
                } else {
                    if (chineseNum != 0) {
                        //Bundle bundle = new Bundle();
                        // bundle放入单词的信息
                        Intent callTest = new Intent(getActivity(), ChineseTest.class);
                        //callTest.putExtras(bundle);
                        startActivity(callTest);
                    } else {
                        Toast.makeText(getActivity(), "考查任务已完成", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button memoryButton = binding.beginMemory;
        memoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int englishNum = mainViewModel.getWordSum().getValue();
                int englishIndex = mainViewModel.getWordIndex().getValue();
                int chineseNum = mainViewModel.getKnowledgeSum().getValue();
                int chineseIndex = mainViewModel.getKnowledgeIndex().getValue();
                if (englishNum != 0) {
                    //Bundle bundle = new Bundle();
                    // bundle放入单词的信息
                    if(englishIndex < englishNum) {
                        Intent callTest = new Intent(getActivity(), EnglishMemory.class);
                        //callTest.putExtras(bundle);
                        startActivity(callTest);
                    } else {
                        if (chineseNum != 0) {
                            if(chineseIndex < chineseNum) {
                                Intent callTest = new Intent(getActivity(), ChineseMemory.class);
                                //callTest.putExtras(bundle);
                                startActivity(callTest);
                            } else {
                                MainActivity.updateData();
                                Intent callTest = new Intent(getActivity(), EnglishMemory.class);
                                //callTest.putExtras(bundle);
                                startActivity(callTest);
                            }
                        } else {
                            MainActivity.updateData();
                            Intent callTest = new Intent(getActivity(), EnglishMemory.class);
                            //callTest.putExtras(bundle);
                            startActivity(callTest);
                        }
                    }

                } else {
                    if (chineseNum != 0) {
                        //Bundle bundle = new Bundle();
                        // bundle放入单词的信息
                        Intent callTest = new Intent(getActivity(), ChineseMemory.class);
                        //callTest.putExtras(bundle);
                        startActivity(callTest);
                    } else {
                        Toast.makeText(getActivity(), "您还没有添加单词和知识点", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        Button todayPlan = binding.ShowTask;
        todayPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromSearch",false);
                Intent showTaskToday = new Intent(getActivity(), ShowTask.class);
                showTaskToday.putExtras(bundle);
                startActivity(showTaskToday);
            }
        });
        ImageView setTaskNum = binding.setTaskNum;
        setTaskNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog();
            }
        });
        return root;
    }

    /**
     * 开启提醒
     */
    //TODO 开启提醒按钮
    private void startRemind() {
        //得到日历实例，主要是为了下面的获取时间
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //c为改了之后的时间
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);

                //02、确定好选择的时间
                //03、设置闹钟  RTC_WAKEUP可以唤醒你的手机
                //04、当时间一到，将执行的响应
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                //上面设置的就是13点25分的时间点
                //获取上面设置的13点25分的毫秒值
                long selectTime = mCalendar.getTimeInMillis();

                // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
                if (systemTime > selectTime) {
                    mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                //AlarmReceiver.class为广播接受者
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("aa", "你好");
                PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                //得到AlarmManager实例
                AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                //**********注意！！下面的两个根据实际需求任选其一即可*********

                /**
                 * 重复提醒
                 * 第一个参数是警报类型；下面有介绍
                 * 第二个参数网上说法不一，很多都是说的是延迟多少毫秒执行这个闹钟，但是我用的刷了MIUI的三星手机的实际效果是与单次提醒的参数一样，即设置的13点25分的时间点毫秒值
                 * 第三个参数是重复周期，也就是下次提醒的间隔 毫秒值 我这里是一天后提醒
                 */
                am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);
            }
        }, hour, minute, true);

        timePickerDialog.show();
        Toast.makeText(getActivity(), "提醒设置完成", Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭提醒
     */
    //TODO 关闭提醒按钮
    private void stopRemind() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
        Toast.makeText(getActivity(), "关闭了提醒", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("设置背诵任务");
        builder.setIcon(android.R.drawable.btn_star);
        String[] items = { "设置单词背诵任务", "设置知识点背诵任务" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case SET_WORD: // 选择设置单词背诵量
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("设置单词背诵量");    //设置对话框标题
                        builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
                        EditText edit = new EditText(getActivity());
                        edit.setHint("输入你的预期背诵数目吧~");
                        edit.setSingleLine(true);
                        builder.setView(edit);
                        builder.setCancelable(false);

                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO：设置每日单词背诵
                                int result = MainActivity.db.setUserWordGoal(mainViewModel.getName().getValue(), edit.getText().toString());
                                if(result == 0) {
                                    Toast.makeText(getActivity(), "请输入正确的数字",
                                            Toast.LENGTH_SHORT).show();
                                } else if (result == 2) {
                                    Toast.makeText(getActivity(), "请输入1-200之间的数字",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    MainActivity.updateData();
                                    Toast.makeText(getActivity(), "你设置每日背诵<" + edit.getText().toString()+">个单词，将从明天的背诵任务开始生效",
                                        Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "你点了取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case SET_KNOWLEDGE:
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("设置知识点背诵量");    //设置对话框标题
                        builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
                        edit = new EditText(getActivity());
                        edit.setHint("输入你的预期背诵数目吧~");
                        edit.setSingleLine(true);
                        builder.setView(edit);
                        builder.setCancelable(false);

                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO：设置每日知识点背诵
                                int result = MainActivity.db.setUserKnowledgeGoal(mainViewModel.getName().getValue(), edit.getText().toString());
                                if(result == 0) {
                                    Toast.makeText(getActivity(), "请输入正确的数字",
                                            Toast.LENGTH_SHORT).show();
                                } else if (result == 2) {
                                    Toast.makeText(getActivity(), "请输入1-200之间的数字",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    MainActivity.updateData();
                                    Toast.makeText(getActivity(), "你设置每日背诵<" +
                                                    edit.getText().toString()+">个知识点，将从明天的背诵任务开始生效",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "你点了取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                }
            }
        });
        builder.create().show();
    }
}