package de.badgersburrow.fragenrondell;

/**
 * Created by Joe on 04.05.2016.
 */
public class aux_listener {

    public interface OnIntegerChangeListener
    {
        public void onIntegerChanged(int newValue);
    }

    public class ObservableInteger
    {
        private OnIntegerChangeListener listener;

        private int value = 0;

        public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
        {
            this.listener = listener;
        }

        public int get()
        {
            return value;
        }

        public void set(int value)
        {
            this.value = value;

            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }
        public void inc(){
            this.value++;
            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }
        public void dec(){
            this.value--;
            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }



    }
}
