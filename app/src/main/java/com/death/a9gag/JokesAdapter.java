package com.death.a9gag;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

class JokesAdapter extends RealmRecyclerViewAdapter<JokesModel,JokesAdapter.ViewHolder > {


    private int[] colors = new int[]{Color.parseColor("#FF1744"),Color.parseColor("#00BFA5"),Color.parseColor("#388E3C"),Color.parseColor("#E65100"), Color.parseColor("#EC407A"),Color.parseColor("#4A148C"),Color.parseColor("#29B6F6"),Color.parseColor("#607D8B")};
    private OrderedRealmCollection<JokesModel> realmCollection;
    JokesAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<JokesModel> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        realmCollection = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_xml, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JokesModel jokes = realmCollection.get(position);
        holder.id.setText(jokes.getId());
        holder.jokes.setText(jokes.getJoke());
        Random rander = new Random();
        int Max = 8;
        int Min = 1;
        holder.layout.setBackgroundColor(colors[(rander.nextInt(Max - Min  + 1) + Min)-1]);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT,  holder.jokes.getText());
                try {
                    context.startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ignored) {

                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView id;
        TextView jokes;
        ImageView button;
        final LinearLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            jokes = (TextView) itemView.findViewById(R.id.jokes);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_bg);
            button = (ImageView) itemView.findViewById(R.id.imagebutton);
        }
    }
}
