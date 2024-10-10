package com.pine.service.request;

import com.pine.Mutable;
import com.pine.inspection.Color;
import com.pine.inspection.FieldDTO;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class UpdateFieldRequest extends AbstractRequest {
    private FieldDTO fieldDTO;
    private Object newValue;

    public UpdateFieldRequest(FieldDTO fieldDTO, Object newValue) {
        this.newValue = newValue;
        this.fieldDTO = fieldDTO;
    }

    public void setFieldDTO(FieldDTO fieldDTO) {
        this.fieldDTO = fieldDTO;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public Message run(WorldRepository repository) {
        try {
            switch (fieldDTO.getType()) {
                case VECTOR2 -> {
                    var field = (Vector2f) fieldDTO.getValue();
                    field.set((float[]) newValue);
                }
                case VECTOR3 -> {
                    var field = (Vector3f) fieldDTO.getValue();
                    field.set((float[]) newValue);
                }
                case VECTOR4 -> {
                    var field = (Vector4f) fieldDTO.getValue();
                    field.set((float[]) newValue);
                }
                case QUATERNION -> {
                    var field = (Quaternionf) fieldDTO.getValue();
                    var cast = (float[]) newValue;
                    field.x = cast[0];
                    field.y = cast[1];
                    field.z = cast[2];
                    field.w = cast[3];
                }
                case COLOR -> {
                    var field = (Color) fieldDTO.getValue();
                    field.set((float[]) newValue);
                }
                default -> fieldDTO.getField().set(fieldDTO.getInstance(), newValue);
            }
            if(fieldDTO.getInstance() instanceof Mutable){
                ((Mutable) fieldDTO.getInstance()).registerChange();
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            return new Message("Could not update field " + fieldDTO.getField().getName(), MessageSeverity.ERROR);
        }
        return null;
    }
}
