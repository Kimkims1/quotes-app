package lexfy.hdstudios.thoughtsapp.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lexfy.hdstudios.thoughtsapp.R;
import lexfy.hdstudios.thoughtsapp.model.QuoteModel;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    private Context context;
    private List<QuoteModel> quoteList;

    @NonNull
    @Override
    public QuoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quotes_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteAdapter.ViewHolder holder, int position) {

        QuoteModel quoteModel = quoteList.get(position);
        String imageUrl;

        holder.title.setText(quoteModel.getTitle());
        holder.description.setText(quoteModel.getDescription());
        // holder.name.setText(quoteModel.getUserName());
        imageUrl = quoteModel.getImage_url();
        /*quoteModel.getTimeAdded();*/

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(quoteModel.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.sunrise)
                .fit()
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description, dateAdded, name;
        public ImageView image;
        String userId;
        String userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.quote_title);
            description = itemView.findViewById(R.id.quote_description);
            dateAdded = itemView.findViewById(R.id.quote_time_posted);
            image = itemView.findViewById(R.id.quote_Iv);


        }
    }
}
