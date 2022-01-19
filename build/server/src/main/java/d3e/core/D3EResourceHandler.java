package d3e.core;

import java.util.List;

import org.springframework.core.io.Resource;

public interface D3EResourceHandler {

    /**
     * Only these methods are allowed as part of the API. save load delete -
     * Optional for now.
     */

    // TODO: Use DFile as param here. FileController's param needs to be changed for
    // that.
    Resource get(String name);

    DFile save(DFile file);

    DFile saveImage(DFile file, List<ImageDimension> resizes);
}
