package com.example.success.ui.person;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.content.Context.ALARM_SERVICE;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.success.AddFriend;
import com.example.success.AlarmReceiver;
import com.example.success.ChangePass;
import com.example.success.CurrentUser;
import com.example.success.DatabaseInterface;
import com.example.success.MainActivity;
import com.example.success.R;
import com.example.success.databinding.FragmentNotificationsBinding;
import com.example.success.databinding.FragmentPersonBinding;
import com.example.success.showFriends;
import com.example.success.ui.dashboard.DashboardViewModel;
import com.example.success.ui.notifications.NotificationsViewModel;
import com.example.success.view.CurveView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class PersonFragment extends Fragment {

    // 提醒管理
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar mCalendar;
    protected static final int UPLOAD_PORTRAIT = 0;
    protected static final int CHANGE_PASS = 1;

    private PersonViewModel mViewModel;
    private FragmentPersonBinding binding;

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DatabaseInterface db = MainActivity.db;
        mViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        final CurveView curveView = binding.curveView;;
//        //  读入近一周的背诵量
//
//        List<String> xList = Arrays.asList("6", "5", "4", "3", "2", "1", "0");
//        Calendar mCalendar = Calendar.getInstance();
//        int date = mCalendar.get(Calendar.DATE);
////        List<String> yList = Arrays.asList("0","50","55","51","53","56","59");
//
//        // weekWord: 用户近一周单词背诵数量
//        int[] weekWord = db.countUserWeekWord(CurrentUser.getUser().getName());
//        // weekKnowledge：用户近一周知识点背诵数量
//        int[] weekKnowledge = db.countUserWeekKnowledge(CurrentUser.getUser().getName());
//        List<String> yList = new ArrayList<>();
//        for (int i = 6; i >= 0; i--) {
//            yList.add(Integer.toString(weekWord[i] + weekKnowledge[i]));
//        }
//        curveView.setData(xList, yList);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView addFriend = getActivity().findViewById(R.id.addFriend_person);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriend.class);
                startActivity(intent);
            }
        });
        ImageView showFriend = getActivity().findViewById(R.id.showFriends_person);
        showFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), showFriends.class);
                startActivity(intent);
            }
        });
        ImageView alarm = getActivity().findViewById(R.id.setAlarm_person);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("请设置提醒");
                builder.setPositiveButton("设置提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRemind();
                    }
                });
                builder.setNegativeButton("取消提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRemind();
                    }
                });
                builder.show();
            }
        });
        TextView userName = (TextView) (getActivity().findViewById(R.id.userName_person));
        ImageView imageView = (ImageView) (getActivity().findViewById(R.id.userPortrait));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(userName.getText().toString());
            }
        });


    }

    /** 开启提醒 */
    private void startRemind(){
        //得到日历实例，主要是为了下面的获取时间
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute=mCalendar.get(Calendar.MINUTE);
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //c为改了之后的时间
                mCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                mCalendar.set(Calendar.MINUTE,minute);

                //02、确定好选择的时间
                //03、设置闹钟  RTC_WAKEUP可以唤醒你的手机
                //04、当时间一到，将执行的响应
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                //上面设置的就是13点25分的时间点
                //获取上面设置的13点25分的毫秒值
                long selectTime = mCalendar.getTimeInMillis();

                // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
                if(systemTime > selectTime) {
                    mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                //AlarmReceiver.class为广播接受者
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, FLAG_MUTABLE);
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
                Toast.makeText(getActivity(), "提醒设置完成, 时间: " + mCalendar.get(Calendar.HOUR_OF_DAY)
                        + "时" + mCalendar.get(Calendar.MINUTE) + "分", Toast.LENGTH_SHORT).show();
            }
        },hour,minute,true);

        timePickerDialog.show();
    }
    /**
     * 关闭提醒
     */
    private void stopRemind(){
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,
                intent, FLAG_MUTABLE);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
        Toast.makeText(getActivity(), "关闭了提醒", Toast.LENGTH_SHORT).show();
    }

    protected void showChooseDialog(String userName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("修改个人信息");
        builder.setIcon(android.R.drawable.btn_star);
        String[] items = { "修改头像", "更改密码" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case UPLOAD_PORTRAIT: // 修改头像
                       //TODO：修改头像
                        break;
                    case CHANGE_PASS:
                        Intent intent = new Intent(getActivity(), ChangePass.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userName",userName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
            }
        });
        builder.create().show();
    }

}