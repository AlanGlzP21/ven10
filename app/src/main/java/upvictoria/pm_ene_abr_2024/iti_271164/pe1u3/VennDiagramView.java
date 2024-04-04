    package upvictoria.pm_ene_abr_2024.iti_271164.pe1u3;

    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.util.AttributeSet;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;

    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.Map;
    import java.util.Set;

    public class VennDiagramView extends View {
        private Paint paint;
        private int numCircles;
        private int[] colors;
        private String[] setNames;

        private int[] elementCounts; // Arreglo para almacenar el número de elementos de cada conjunto
        private int[] intersectionElementCounts; // Arreglo para almacenar el número de elementos en común entre las intersecciones
        private int[] intersectionCounts;
        private Set<String>[] elementSets; // Conjuntos para almacenar los elementos de cada conjunto
        private OnIntersectionClickListener intersectionClickListener;
         private Map<Integer, String> elementsMap;

        public VennDiagramView(Context context) {
            super(context);
            init();
        }

        public void setElementSets(Set<String>[] elementSets) {
            this.elementSets = elementSets;
        }


      public VennDiagramView(Context context, AttributeSet attrs) {
             super(context, attrs);
             init();
         }

        private void init() {
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
             elementsMap = new HashMap<>();

            numCircles = 6; // Se cambia el número de círculos a 6
            colors = new int[]{
                    Color.argb(128, 255, 0, 0),
                    Color.argb(128, 0, 255, 0),
                    Color.argb(128, 0, 0, 255),
                    Color.argb(128, 255, 255, 0),
                    Color.argb(128, 255, 0, 255), // Se agrega un nuevo color
                    Color.argb(128, 0, 255, 255)  // Se agrega otro nuevo color
            };
            setNames = generateSequentialSetNames(numCircles);

            // Inicializa los conjuntos de elementos
            elementSets = new Set[numCircles];
            for (int i = 0; i < numCircles; i++) {
                elementSets[i] = new HashSet<>();
            }
        }

        private String[] generateSequentialSetNames(int numSets) {
            String[] sequentialSetNames = new String[numSets];
            char currentChar = 'A';
            for (int i = 0; i < numSets; i++) {
                sequentialSetNames[i] = "Set " + currentChar;
                currentChar++;
            }
            return sequentialSetNames;
        }

        public interface OnIntersectionClickListener {
            void onIntersectionClick(int circleIndex1, int circleIndex2);
        }

        public void setOnIntersectionClickListener(OnIntersectionClickListener listener) {
            this.intersectionClickListener = listener;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            float ovalWidth = width / 2f;
            float ovalHeight = height / 2f;

            float distance = ovalWidth / 2f;
            float offsetX = width / 2f;
            float offsetY = height / 2f;

            float[] centersX = {
                    offsetX - distance / 2,
                    offsetX + distance,
                    offsetX - distance / 2,
                    offsetX + distance / 2,
                    offsetX - distance / 2,
                    offsetX + distance / 2
            };

            float[] centersY = {
                    offsetY - distance / 2,
                    offsetY - distance / 2,
                    offsetY + distance / 2,
                    offsetY + distance / 2,
                    offsetY - distance,
                    offsetY - distance
            };

            // Dibujar los círculos
            for (int i = 0; i < numCircles; i++) {
                paint.setColor(colors[i % colors.length]);
                float left = centersX[i] - ovalWidth / 2;
                float top = centersY[i] - ovalHeight / 2;
                float right = centersX[i] + ovalWidth / 2;
                float bottom = centersY[i] + ovalHeight / 2;
                canvas.drawOval(left, top, right, bottom, paint);

                if (setNames != null && setNames.length > i && setNames[i] != null) {
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(30);
                    float textWidth = paint.measureText(setNames[i]);
                    float textX = centersX[i] - textWidth / 2;
                    float textY = (i < 4) ? (centersY[i] - ovalHeight / 2 - 40) : (centersY[i] + ovalHeight / 2 + 40);
                    canvas.drawText(setNames[i], textX, textY, paint);

                    // Dibujar el número de elementos en el centro del óvalo
                    if (elementCounts != null && elementCounts.length > i) {
                        String countText = String.valueOf(elementCounts[i]);
                        float countTextWidth = paint.measureText(countText);
                        float countTextX = centersX[i] - countTextWidth / 2;
                        float countTextY = centersY[i] + (paint.descent() + paint.ascent()) / 2;
                        canvas.drawText(countText, countTextX, countTextY, paint);
                    }
                }
            }

            // Dibujar los contadores de intersecciones
            paint.setColor(Color.RED);
            paint.setTextSize(30);

            if (intersectionCounts != null && intersectionCounts.length > 0) {
                int index = 0;
                for (int i = 0; i < numCircles; i++) {
                    for (int j = i + 1; j < numCircles; j++) {
                        float intersectionX = (centersX[i] + centersX[j]) / 2;
                        float intersectionY = (centersY[i] + centersY[j]) / 2;

                        // Asegurar que el índice esté dentro de los límites del array intersectionCounts
                        if (index < intersectionCounts.length) {
                            String intersectionText = String.valueOf(intersectionCounts[index]);
                            float textWidth = paint.measureText(intersectionText);
                            float textX = intersectionX - textWidth / 2;
                            float textY = intersectionY + (paint.descent() + paint.ascent()) / 2;
                            canvas.drawText(intersectionText, textX, textY, paint);
                        }
                        index++;
                    }
                }
            }
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            int width = getWidth();
            int height = getHeight();

            float ovalWidth = width / 2f;
            float ovalHeight = height / 2f;

            float distance = ovalWidth / 2f;
            float offsetX = width / 2f;
            float offsetY = height / 2f;

            for (int i = 0; i < numCircles; i++) {
                float centerX = offsetX + (i % 2 == 0 ? -distance / 2 : distance / 2);
                float centerY = offsetY + (i < 4 ? -distance / 2 : distance / 2);
                float radius = ovalWidth / 2f;

                // Calcular la distancia del punto de contacto al centro del círculo
                float distanceToCenter = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                // Si la distancia es menor o igual al radio, el punto está dentro del círculo
                if (distanceToCenter <= radius) {
                    // Obtener el conjunto de elementos correspondiente
                    Set<String> elementSet = elementSets != null && i < elementSets.length ? elementSets[i] : new HashSet<>();
                    // Obtener el nombre del conjunto
                    String setName = setNames != null && i < setNames.length ? setNames[i] : "Set " + (char) ('A' + i);
                    // Obtener el recuento de elementos del conjunto
                    int elementCount = elementCounts != null && i < elementCounts.length ? elementCounts[i] : 0;
                    // Convertir el conjunto de elementos a una cadena para mostrar
                    String elements = elementSet.isEmpty() ? "Sin elementos" : String.join(", ", elementSet);

                    // Crear el mensaje del diálogo con la información del conjunto
                    String message = "Nombre del conjunto: " + setName + "\nRecuento de elementos: " + elementCount + "\nElementos:\n" + elements;

                    // Crear y mostrar el diálogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Información del conjunto");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cerrar el diálogo si se hace clic en OK
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                    return true; // Indica que el evento fue manejado
                }
            }

            return super.onTouchEvent(event); // Si el punto no está dentro de ningún círculo, dejar que el padre maneje el evento
        }




        private int getIntersectionIndex(int i, int j) {
            if (i < j) {
                return numCircles * i + j - i * (i + 1) / 2 - 1;
            } else {
                return numCircles * j + i - j * (j + 1) / 2 - 1;
            }
        }

        public void setNumCircles(int numCircles) {
            this.numCircles = numCircles;
            invalidate();
        }

        public int getNumCircles() {
            return numCircles;
        }

        public void setIntersectionCounts(int[] intersectionCounts) {
            this.intersectionCounts = intersectionCounts;
            invalidate(); // Vuelve a dibujar la vista para reflejar los cambios
        }

        public void setColors(int[] colors) {
            if (colors != null) {
                this.colors = new int[colors.length];
                for (int i = 0; i < colors.length; i++) {
                    int alpha = 128;
                    int red = Color.red(colors[i]);
                    int green = Color.green(colors[i]);
                    int blue = Color.blue(colors[i]);
                    this.colors[i] = Color.argb(alpha, red, green, blue);
                }
            }
            invalidate();
        }

        public String[] getSetNames() {
            return setNames;
        }

        public void setSetNames(String[] setNames) {
            this.setNames = setNames;
            invalidate();
        }

        public void setElementCounts(int[] elementCounts) {
            this.elementCounts = elementCounts;
            invalidate();
        }

        public void setIntersectionElementCounts(int[] intersectionElementCounts) {
            this.intersectionElementCounts = intersectionElementCounts;
            invalidate();
        }
    }
