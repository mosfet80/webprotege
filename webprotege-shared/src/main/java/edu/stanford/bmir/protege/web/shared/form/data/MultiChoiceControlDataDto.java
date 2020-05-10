package edu.stanford.bmir.protege.web.shared.form.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.form.field.ChoiceDescriptor;
import edu.stanford.bmir.protege.web.shared.form.field.ChoiceDescriptorDto;
import edu.stanford.bmir.protege.web.shared.form.field.MultiChoiceControlDescriptor;

import javax.annotation.Nonnull;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

@AutoValue
@GwtCompatible(serializable = true)
public abstract class MultiChoiceControlDataDto implements FormControlDataDto {

    @Nonnull
    public static MultiChoiceControlDataDto get(@Nonnull MultiChoiceControlDescriptor descriptor,
                                                ImmutableList<ChoiceDescriptorDto> choices,
                                                @Nonnull ImmutableList<PrimitiveFormControlDataDto> values) {
        return new AutoValue_MultiChoiceControlDataDto(descriptor, choices, values);
    }

    @JsonProperty("descriptor")
    @Nonnull
    public abstract MultiChoiceControlDescriptor getDescriptor();

    @Nonnull
    public abstract ImmutableList<ChoiceDescriptorDto> getAvailableChoices();

    @JsonProperty("values")
    @Nonnull
    public abstract ImmutableList<PrimitiveFormControlDataDto> getValues();

    @Override
    public <R> R accept(FormControlDataDtoVisitorEx<R> visitor) {
        return visitor.visit(this);
    }

    @Nonnull
    @Override
    public MultiChoiceControlData toFormControlData() {
        return MultiChoiceControlData.get(getDescriptor(),
                getValues().stream().map(PrimitiveFormControlDataDto::toPrimitiveFormControlData).collect(toImmutableList()));
    }
}
