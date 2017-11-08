package com.google.firebase.example.fireeats.common;

import android.annotation.SuppressLint;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The of the ViewDataBinding
 */
public abstract class DataListAdapter<T, V extends ViewDataBinding>
        extends RecyclerView.Adapter<DataViewHolder<V>> {

    protected final OnItemClickedListener<T> listener;
    @Nullable
    private List<T> items;
    // each time data is set, we update this variable so that if DiffUtil calculation returns
    // after repetitive updates, we can ignore the old calculation
    private int dataVersion = 0;

    public DataListAdapter(final OnItemClickedListener<T> listener) {
        this.listener = Objects.requireNonNull(listener, "listener cannot be null");
    }

    @Override
    public final DataViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        V binding = createBinding(inflater, parent);
        return new DataViewHolder<>(binding);
    }

    protected abstract V createBinding(LayoutInflater inflater, ViewGroup parent);

    @Override
    public final void onBindViewHolder(DataViewHolder<V> holder, int position) {
        //noinspection ConstantConditions
        bind(holder.binding, items.get(position));
        holder.binding.executePendingBindings();
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void replace(final List<T> update) {
        dataVersion++;
        if (items == null) {
            if (update == null) {
                return;
            }
            items = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = items.size();
            items = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = items;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return /*Objects.equals(oldItem, newItem)*/ false;
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return /*Objects.equals(oldItem, newItem)*/ false;
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }
                    items = update;
                    diffResult.dispatchUpdatesTo(DataListAdapter.this);

                }
            }.execute();
        }
    }

    protected abstract void bind(V binding, T item);

    protected boolean areItemsTheSame(T oldItem, T newItem) {
        return Objects.equals(oldItem, newItem);
    }

    protected boolean areContentsTheSame(T oldItem, T newItem) {
        return Objects.equals(oldItem, newItem);
    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
