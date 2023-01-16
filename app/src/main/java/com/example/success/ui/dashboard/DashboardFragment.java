package com.example.success.ui.dashboard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.success.CurrentUser;
import com.example.success.DatabaseInterface;
import com.example.success.MainActivity;
import com.example.success.MainViewModel;
import com.example.success.R;
import com.example.success.Sign_up;
import com.example.success.Upload;
import com.example.success.Upload_Word;
import com.example.success.databinding.FragmentDashboardBinding;
import com.example.success.takePhoto;
import com.example.success.view.CurveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DatabaseInterface db = MainActivity.db;
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final CurveView curveView = binding.curveView;
        final TextView textView = binding.textDashboard;
        textView.setText("一周背诵量");
        //  读入近一周的背诵量

        List<String> xList = Arrays.asList("6", "5", "4", "3", "2", "1", "0");
        Calendar mCalendar = Calendar.getInstance();
        int date = mCalendar.get(Calendar.DATE);
//        List<String> yList = Arrays.asList("0","50","55","51","53","56","59");

        // weekWord: 用户近一周单词背诵数量
        int[] weekWord = db.countUserWeekWord(CurrentUser.getUser().getName());
        // weekKnowledge：用户近一周知识点背诵数量
        int[] weekKnowledge = db.countUserWeekKnowledge(CurrentUser.getUser().getName());
        List<String> yList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            yList.add(Integer.toString(weekWord[i] + weekKnowledge[i]));
        }
        curveView.setData(xList, yList);
        final Button add_word = binding.addWordButton;
        add_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent(getActivity(), Upload_Word.class);
                jump.putExtra("id", (Long) null);
                jump.putExtra("fromEdit",false);
                startActivity(jump);
            }
        });
        final Button add_knowledge = binding.addKnowledgeButton;
        add_knowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent(getActivity(), Upload.class);
                jump.putExtra("id", (Long) null);
                jump.putExtra("fromEdit",false);
                startActivity(jump);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}