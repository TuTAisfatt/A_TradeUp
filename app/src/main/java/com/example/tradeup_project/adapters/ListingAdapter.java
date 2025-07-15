package com.example.tradeup_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup_project.R;
import com.example.tradeup_project.models.Listing;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<Listing> listings;
    private OnListingClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnListingClickListener {
        void onListingClick(Listing listing);
        void onEditClick(Listing listing);
        void onDeleteClick(Listing listing);
        void onStatusChange(Listing listing, String newStatus);
    }

    public ListingAdapter(List<Listing> listings, OnListingClickListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.bind(listing);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public void updateData(List<Listing> newListings) {
        this.listings = newListings;
        notifyDataSetChanged();
    }

    class ListingViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView imageView;
        private TextView titleText, priceText, statusText, dateText, viewsText;
        private ImageView moreButton;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.listingImage);
            titleText = itemView.findViewById(R.id.titleText);
            priceText = itemView.findViewById(R.id.priceText);
            statusText = itemView.findViewById(R.id.statusText);
            dateText = itemView.findViewById(R.id.dateText);
            viewsText = itemView.findViewById(R.id.viewsText);
            moreButton = itemView.findViewById(R.id.moreButton);
        }

        public void bind(Listing listing) {
            titleText.setText(listing.getTitle());
            priceText.setText(String.format("$%.2f", listing.getPrice()));
            statusText.setText(listing.getStatus());
            dateText.setText(dateFormat.format(new Date(listing.getCreatedAt())));
            viewsText.setText(listing.getViews() + " views");

            // Set status color
            switch (listing.getStatus()) {
                case "Available":
                    statusText.setTextColor(itemView.getContext().getColor(R.color.success));
                    break;
                case "Sold":
                    statusText.setTextColor(itemView.getContext().getColor(R.color.error));
                    break;
                case "Paused":
                    statusText.setTextColor(itemView.getContext().getColor(R.color.warning));
                    break;
            }

            // Load image
            if (listing.getImageUrls() != null && !listing.getImageUrls().isEmpty()) {
                // TODO: Load with Glide
                // Glide.with(itemView.getContext())
                //      .load(listing.getImageUrls().get(0))
                //      .into(imageView);
            }

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onListingClick(listing);
                }
            });

            moreButton.setOnClickListener(v -> showPopupMenu(v, listing));
        }

        private void showPopupMenu(View view, Listing listing) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.menu_listing_options);

            // Modify menu based on listing status
            if (listing.getStatus().equals("Available")) {
                popup.getMenu().findItem(R.id.action_mark_available).setVisible(false);
            } else if (listing.getStatus().equals("Sold")) {
                popup.getMenu().findItem(R.id.action_mark_sold).setVisible(false);
            } else if (listing.getStatus().equals("Paused")) {
                popup.getMenu().findItem(R.id.action_pause).setVisible(false);
            }

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.action_edit) {
                    if (listener != null) listener.onEditClick(listing);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    if (listener != null) listener.onDeleteClick(listing);
                    return true;
                } else if (itemId == R.id.action_mark_available) {
                    if (listener != null) listener.onStatusChange(listing, "Available");
                    return true;
                } else if (itemId == R.id.action_mark_sold) {
                    if (listener != null) listener.onStatusChange(listing, "Sold");
                    return true;
                } else if (itemId == R.id.action_pause) {
                    if (listener != null) listener.onStatusChange(listing, "Paused");
                    return true;
                }

                return false;
            });

            popup.show();
        }
    }
}