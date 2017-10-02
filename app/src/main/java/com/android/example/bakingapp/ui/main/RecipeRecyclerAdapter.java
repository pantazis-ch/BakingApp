package com.android.example.bakingapp.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.Recipe;
import com.android.example.bakingapp.data.RecipesContract;


public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.RecipeHolder> {


    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    private final RecipeAdapterOnClickHandler mClickHandler;

    private Cursor mCursor;


    public RecipeRecyclerAdapter(RecipeAdapterOnClickHandler clickHandler) {
        //this.mCursor = cursor;
        this.mClickHandler = clickHandler;
    }

    public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;

        public RecipeHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.tv_recipe_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);

            Recipe recipe = new Recipe(mCursor);

            mClickHandler.onClick(recipe);

        }
    }

    @Override
    public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForListItem = R.layout.recipe_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new RecipeHolder(view);

    }

    @Override
    public void onBindViewHolder(RecipeHolder holder, int position) {

        mCursor.moveToPosition(position);

        holder.nameTextView.setText(mCursor.getString(RecipesContract.RecipeEntry.POSITION_NAME));

    }

    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();

    }

    public void setCursor(Cursor cursor) {

        if (mCursor != cursor) {
            this.mCursor = cursor;
            this.notifyDataSetChanged();
        }

    }

}
