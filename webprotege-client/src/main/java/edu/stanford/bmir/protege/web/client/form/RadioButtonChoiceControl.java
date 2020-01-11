package edu.stanford.bmir.protege.web.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RadioButton;
import edu.stanford.bmir.protege.web.resources.WebProtegeClientBundle;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.form.data.FormControlData;
import edu.stanford.bmir.protege.web.shared.form.data.PrimitiveFormControlData;
import edu.stanford.bmir.protege.web.shared.form.data.SingleChoiceControlData;
import edu.stanford.bmir.protege.web.shared.form.field.ChoiceDescriptor;
import edu.stanford.bmir.protege.web.shared.form.field.SingleChoiceControlDescriptor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/16
 */
public class RadioButtonChoiceControl extends Composite implements SingleChoiceControl {

    private static int nameCounter = 0;

    private ValueChangeHandler<Boolean> radioButtonValueChangedHandler;

    private SingleChoiceControlDescriptor descriptor;

    interface RadioButtonChoiceControlUiBinder extends UiBinder<HTMLPanel, RadioButtonChoiceControl> {

    }

    private static RadioButtonChoiceControlUiBinder ourUiBinder = GWT.create(RadioButtonChoiceControlUiBinder.class);

    @UiField
    HTMLPanel container;

    private Map<RadioButton, ChoiceDescriptor> choiceButtons = new LinkedHashMap<>();

    private Optional<PrimitiveFormControlData> defaultChoice = Optional.empty();

    @Inject
    public RadioButtonChoiceControl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        radioButtonValueChangedHandler = event -> ValueChangeEvent.fire(RadioButtonChoiceControl.this, getValue());
    }

    @Override
    public void setDescriptor(@Nonnull SingleChoiceControlDescriptor descriptor) {
        this.descriptor = descriptor;
        setChoices(descriptor.getChoices());
        descriptor.getDefaultChoice().ifPresent(this::setDefaultChoice);
    }

    private void setChoices(List<ChoiceDescriptor> choices) {
        container.clear();
        choiceButtons.clear();
        nameCounter++;
        String langTag = LocaleInfo.getCurrentLocale().getLocaleName();
        for(ChoiceDescriptor descriptor : choices) {
            RadioButton radioButton = new RadioButton("Choice" + nameCounter, new SafeHtmlBuilder().appendHtmlConstant(descriptor.getLabel().get(langTag)).toSafeHtml());
            radioButton.addStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
            radioButton.addValueChangeHandler(radioButtonValueChangedHandler);
            radioButton.addFocusHandler(event -> {
                radioButton.addStyleName(WebProtegeClientBundle.BUNDLE.style().focusBorder());
                radioButton.removeStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
            });
            radioButton.addBlurHandler(event -> {
                radioButton.addStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
                radioButton.removeStyleName(WebProtegeClientBundle.BUNDLE.style().focusBorder());
            });
            container.add(radioButton);
            choiceButtons.put(radioButton, descriptor);
        }
        selectDefaultChoice();
    }

    private void setDefaultChoice(@Nonnull ChoiceDescriptor choice) {
        defaultChoice = Optional.of(choice.getValue());
    }

    private void selectDefaultChoice() {
        setChoice(defaultChoice);
    }

    @Override
    public void setValue(FormControlData value) {
        if(value instanceof SingleChoiceControlData) {
            Optional<PrimitiveFormControlData> choice = ((SingleChoiceControlData) value).getChoice();
            setChoice(choice);
        }
        else {
            clearValue();
        }
    }

    public void setChoice(Optional<PrimitiveFormControlData> choice) {
        if(choice.isPresent()) {
            for(RadioButton radioButton : choiceButtons.keySet()) {
                ChoiceDescriptor choiceDescriptor = choiceButtons.get(radioButton);
                if(choiceDescriptor.getValue().equals(choice.get())) {
                    radioButton.setValue(true);
                }
            }
        }
        else {
            clearValue();
        }
    }

    @Override
    public void clearValue() {
        for(RadioButton radioButton : choiceButtons.keySet()) {
            radioButton.setValue(false);
        }
        selectDefaultChoice();
    }

    @Override
    public Optional<FormControlData> getValue() {
        for(RadioButton radioButton : choiceButtons.keySet()) {
            if(radioButton.getValue()) {
                ChoiceDescriptor choiceDescriptor = choiceButtons.get(radioButton);
                return Optional.of(SingleChoiceControlData.get(descriptor, choiceDescriptor.getValue()));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<FormControlData>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isWellFormed() {
        return true;
    }

    @Override
    public void requestFocus() {
        choiceButtons.keySet()
                     .stream()
                     .findFirst()
                     .ifPresent(radioButton -> radioButton.setFocus(true));
    }
}