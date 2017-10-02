package com.android.example.bakingapp.ui.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.Ingredient;
import com.android.example.bakingapp.data.Step;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface StepOnClickHandler {
        void onClick(Step step, int stepPosition);
    }

    private final DetailRecyclerAdapter.StepOnClickHandler mClickHandler;

    private static final int HEADER_VIEW_TYPE = 255;
    private static final int INGREDIENT_VIEW_TYPE = 256;
    private static final int STEP_VIEW_TYPE = 257;

    private Context context;

    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    private int servings = 0;

    public DetailRecyclerAdapter(Context context, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, DetailRecyclerAdapter.StepOnClickHandler clickHandler, int servings) {
        this.context = context;
        this.ingredients = ingredients;
        this.steps = steps;
        this.mClickHandler = clickHandler;
        this.servings = servings;
    }

    /*public void setIngredients(ArrayList<Ingredient> ingredients) {
        if (this.ingredients != ingredients) {
            this.ingredients = ingredients;
            this.notifyDataSetChanged();
        }
    }

    public void setSteps(ArrayList<Step> steps) {
        if (this.steps != steps) {
            this.steps = steps;
            this.notifyDataSetChanged();
        }
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        switch (viewType){
            case HEADER_VIEW_TYPE:
                View labelView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_list_item, parent, false);
                viewHolder = new HeaderViewHolder(labelView);
                break;
            case INGREDIENT_VIEW_TYPE:
                View ingredientListView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ingredient_list_item, parent, false);
                viewHolder = new IngredientViewHolder(ingredientListView);
                break;
            case STEP_VIEW_TYPE:
                View stepView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.step_list_item, parent, false);
                viewHolder = new StepViewHolder(stepView);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();

        switch (viewType){
            case HEADER_VIEW_TYPE:

                if(position == 0) {
                    String ingredientsLabel = context.getString(R.string.label_ingredients);
                    String servingsLabel = servings + " " + context.getString(R.string.label_servings);

                    ((HeaderViewHolder)holder).headerTextView.setText( ingredientsLabel + " ( " + servingsLabel +" )");
                } else {
                    ((HeaderViewHolder)holder).headerTextView.setText(context.getString(R.string.label_steps));
                }
                break;

            case INGREDIENT_VIEW_TYPE:

                double quantity = ingredients.get(position - 1).getQuantity();
                DecimalFormat quantityFormat = new DecimalFormat("0.#");

                String measure = ingredients.get(position - 1).getMeasure();
                if (measure.equals("UNIT")) {
                    measure = "";
                }

                String ingredient = ingredients.get(position - 1).getIngredient();

                ((IngredientViewHolder)holder).quantityTextView.setText(quantityFormat.format(quantity) + " " + measure);

                ((IngredientViewHolder)holder).ingredientsTextView.setText(ingredient);

                break;

            case STEP_VIEW_TYPE:

                String step = steps.get(position - ingredients.size() - 2).getShortDescription();

                ((StepViewHolder)holder).stepNumberTextView.setText(context.getString(R.string.label_step) + " " + (position - ingredients.size() - 1));

                ((StepViewHolder)holder).stepTextView.setText(step);

                break;
        }
    }

    @Override
    public int getItemCount() {

        if(steps!=null && steps.size()!=0 && ingredients!=null && ingredients.size()!=0) {
            return steps.size() + ingredients.size() + 2;
        } else if (ingredients!=null && ingredients.size()!=0) {
            return ingredients.size() + 2;
        } else if (steps!=null && steps.size()!=0) {
            return steps.size() + 2;
        }  else {
            return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (viewIsHeader(position)) {
            return HEADER_VIEW_TYPE;
        } else if(viewIsIngredient(position)){
            return INGREDIENT_VIEW_TYPE;
        } else if(viewIsStep(position)) {
            return STEP_VIEW_TYPE;
        } else {
            return -1;
        }
    }

    private boolean viewIsHeader(int position) {
        if(position == 0){
            return true;
        }

        if(ingredients != null && ingredients.size()!=0 ) {
            if(position == ingredients.size() + 1 ) {
                return true;
            } else {
                return false;
            }
        } else {
            if (position == 1) {
                return true;
            }
        }

        return false;

    }

    private boolean viewIsIngredient(int position) {
        if(ingredients != null && ingredients.size() != 0 ) {
            if(position < ingredients.size() + 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean viewIsStep(int position) {
        if(steps != null && steps.size() != 0 ) {
            if( ingredients != null && ingredients.size() !=0 ){
                if(position > ingredients.size() + 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView quantityTextView;
        TextView ingredientsTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            quantityTextView = (TextView) itemView.findViewById(R.id.tv_ingredient_quantity);
            ingredientsTextView = (TextView) itemView.findViewById(R.id.tv_ingredient);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView headerTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            headerTextView = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout stepPanel;

        TextView stepNumberTextView;
        TextView stepTextView;

        public StepViewHolder(View itemView) {
            super(itemView);

            stepPanel = (LinearLayout) itemView.findViewById(R.id.step_panel);

            stepNumberTextView = (TextView) itemView.findViewById(R.id.tv_step_number);
            stepTextView = (TextView) itemView.findViewById(R.id.tv_step);

            stepPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            Step step = steps.get(adapterPosition - ingredients.size() - 2);

            mClickHandler.onClick(step, adapterPosition - ingredients.size() - 2);
        }
    }

}
