package me.rotatingrecyclerview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.rotatingrecyclerview.animators.MySimpleItemAnimator;
import me.rotatingrecyclerview.snappy.SnappyLinearLayoutManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private Adapter adapter;
//    private LinearLayoutManager linearLayoutManager;
    private SnappyLinearLayoutManager snappyLinearLayoutManager;
    private ScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        List<String> elems = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            elems.add("Text" + i);
        }
        adapter = new Adapter(this, elems);
        snappyLinearLayoutManager = new SnappyLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

//        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setLayoutManager(snappyLinearLayoutManager);

        recyclerView.setAdapter(adapter);
        recyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                Log.d(TAG, "drawing order " + i);
                return scrollListener.getViewPosition(i, childCount);
            }
        });

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
                Adapter.ViewHolder holder = (Adapter.ViewHolder) parent.getChildViewHolder(view);
                outRect.set(-holder.offset, 0, -holder.offset, 0);
                Log.d(TAG, "Item offsets " + holder.offset);
            }
        });

        scrollListener = new ScrollListener();


        recyclerView.setItemAnimator(new MySimpleItemAnimator());
        recyclerView.addOnScrollListener(scrollListener);

    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final Random rnd;
            // each data item is just a string in this case
            public TextView mTextView;
            public int offset = 0;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
                rnd = new Random();
                Paint p = new Paint();
                p.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                mTextView.setBackgroundColor(p.getColor());
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public Adapter(Context context, List<String> mDataSet) {
            mDataset = mDataSet;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder((TextView)v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset.get(position));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
