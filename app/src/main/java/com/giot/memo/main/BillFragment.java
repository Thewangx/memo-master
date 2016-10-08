package com.giot.memo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giot.memo.R;
import com.giot.memo.add.AddActivity;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.util.DaoUtil;
import com.giot.memo.view.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * bill fragment
 * Created by reed on 16/8/9.
 */
public class BillFragment extends Fragment {

    @BindView(R.id.recycler_bill)
    public RecyclerView billRecycler;

    private Unbinder unbinder;
    private BillAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, mView);
        if (mAdapter == null) {
            mAdapter = new BillAdapter();
        }
        billRecycler.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        billRecycler.setAdapter(mAdapter);
        billRecycler.setItemAnimator(new DefaultItemAnimator());
        billRecycler.addItemDecoration(new DividerItemDecoration(mView.getContext()));
        mAdapter.setOnItemClickListener(new BillAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("修改该条账单?");
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), AddActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bill", mAdapter.getBills().get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();*/
                Intent intent = new Intent(getActivity(), AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bill", mAdapter.getBills().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onDelClick(View view, int position) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("您确定删除该条账单?");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();*/

                DaoUtil.getBillDao().delete(mAdapter.getBills().get(position));
                mAdapter.getBills().remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }

    public void setData(List<Bill> bills) {
        if (mAdapter == null) {
            mAdapter = new BillAdapter();
        }
        mAdapter.setBills(bills);
        //mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
        if (billRecycler != null) {
            billRecycler.setAdapter(mAdapter);
        }
    }
}
