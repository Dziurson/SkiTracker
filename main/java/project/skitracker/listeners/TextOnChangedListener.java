package project.skitracker.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by jakub on 28.12.2016.
 */
public abstract class TextOnChangedListener<T extends TextView> implements TextWatcher
{
    private T target;

    public TextOnChangedListener(T target)
    {
        this.target = target;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
        //Here comes code before changing TextView
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
        //Here comes code while TextView is changing
    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        onTextChanged(target,editable);
    }

    public abstract void onTextChanged(T target, Editable editable);

}
