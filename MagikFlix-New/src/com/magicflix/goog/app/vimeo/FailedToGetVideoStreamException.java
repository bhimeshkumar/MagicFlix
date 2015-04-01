/**
 * 
 */
package com.magicflix.goog.app.vimeo;

@SuppressWarnings("serial")
public class FailedToGetVideoStreamException extends VideoStreamRequestException {
    
    public FailedToGetVideoStreamException(String description) {
        super(description);
    }
    
}