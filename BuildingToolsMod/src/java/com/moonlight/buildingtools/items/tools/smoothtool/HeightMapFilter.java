package com.moonlight.buildingtools.items.tools.smoothtool;

import java.awt.image.Kernel;
import static com.google.common.base.Preconditions.checkNotNull;

public class HeightMapFilter {
	private Kernel kernel;

    /**
     * Construct the HeightMapFilter object.
     * 
     * @param kernel the kernel
     */
    public HeightMapFilter(Kernel kernel) {
        checkNotNull(kernel);
        this.kernel = kernel;
    }

    /**
     * Construct the HeightMapFilter object.
     * 
     * @param kernelWidth the width
     * @param kernelHeight the height
     * @param kernelData the data
     */
    public HeightMapFilter(int kernelWidth, int kernelHeight, float[] kernelData) {
        checkNotNull(kernelData);
        this.kernel = new Kernel(kernelWidth, kernelHeight, kernelData);
    }

    /**
     * @return the kernel
     */
    public Kernel getKernel() {
        return kernel;
    }

    /**
     * Set Kernel
     * 
     * @param kernel the kernel
     */
    public void setKernel(Kernel kernel) {
        checkNotNull(kernel);

        this.kernel = kernel;
    }

    /**
     * Filter with a 2D kernel
     *
     * @param inData the data
     * @param width the width
     * @param height the height
     *
     * @return the modified height map
     */
    public int[] filter(int[] inData, int width, int height) {
        checkNotNull(inData);

        int index = 0;
        float[] matrix = kernel.getKernelData(null);
        int[] outData = new int[inData.length];

        int kh = kernel.getHeight();
        int kw = kernel.getWidth();
        int kox = kernel.getXOrigin();
        int koy = kernel.getYOrigin();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float z = 0;

                for (int ky = 0; ky < kh; ++ky) {
                    int offsetY = y + ky - koy;
                    // Clamp coordinates inside data
                    if (offsetY < 0 || offsetY >= height) {
                        offsetY = y;
                    }

                    offsetY *= width;

                    int matrixOffset = ky * kw;
                    for (int kx = 0; kx < kw; ++kx) {
                        float f = matrix[matrixOffset + kx];
                        if (f == 0) continue;

                        int offsetX = x + kx - kox;
                        // Clamp coordinates inside data
                        if (offsetX < 0 || offsetX >= width) {
                            offsetX = x;
                        }

                        z += f * inData[offsetY + offsetX];
                    }
                }
                outData[index++] = (int) (z + 0.5);
            }
        }
        return outData;
    }
}
