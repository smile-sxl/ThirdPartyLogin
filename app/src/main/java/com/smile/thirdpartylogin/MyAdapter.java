package com.smile.thirdpartylogin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private LoginClickListener mListener;
    private Context context;
    private List<Login> logins;
    private int itemHeight = 0;

    public MyAdapter(Context context, List<Login> logins, int itemHeight) {
        this.context = context;
        this.logins = logins;
        this.itemHeight = itemHeight;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_login, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = itemHeight;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Login login = logins.get(position);
        if (login != null) {
            holder.ivLogin.setImageResource(login.getLoginImage());
            holder.tvLogin.setText(login.getName());
            holder.llLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemLoginClick(position, v);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return logins.size();
    }

    public void setLoginClickListener(LoginClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivLogin;
        private TextView tvLogin;
        private LinearLayout llLogin;

        public ViewHolder(View itemView) {
            super(itemView);
            llLogin = (LinearLayout) itemView.findViewById(R.id.llLogin);
            ivLogin = (ImageView) itemView.findViewById(R.id.ivLogin);
            tvLogin = (TextView) itemView.findViewById(R.id.tvLogin);
        }
    }
}