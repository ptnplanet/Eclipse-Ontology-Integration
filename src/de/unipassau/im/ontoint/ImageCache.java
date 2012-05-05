package de.unipassau.im.ontoint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * A lazy-loading image cache.
 *
 * @see
 * Clayberg, Eric ; Rubel, Dan: Eclipse Plug-ins (3rd Edition). 3. Addison-
 * Wesley Professional, 2008 P. 347
 */
public final class ImageCache {

    /**
     * The map mappin descriptors to images.
     */
    private Map<ImageDescriptor, Image> imageMap;

    /**
     * Creates a new image cache.
     */
    public ImageCache() {
        this.imageMap = new HashMap<ImageDescriptor, Image>();
    }

    /**
     * Will lazy-load images as requested.
     *
     * @param descriptor the descriptor of the image
     * @return the image
     */
    public Image getImage(final ImageDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }

        Image image = (Image) this.imageMap.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            this.imageMap.put(descriptor, image);
        }
        return image;
    }

    /**
     * Clear the cache.
     */
    public void dispose() {
        for (Iterator<Image> it = this.imageMap.values().iterator();
                it.hasNext();) {
            it.next().dispose();
        }
        this.imageMap.clear();
    }

}
