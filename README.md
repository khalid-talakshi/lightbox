# A3: Lightbox
Khalid Talakshi \
20772044 kntalaks \
kotlinc-jvm 1.5.21 \
Windows 10 20H2

## Added Features
- Multiple Image Selection (Note: There is a load time when adding mulitple images)

## Technical Notes
- Tile Mode: When in tile mode, the images will automatically flow when the window is resized. As soon as you drag an image, it will change back to cascade mode
- Cascade Mode: When resizing the window in cascade mode, the images will stay in place, and scrollbars will appear
- min window size: 800x600
- max window size: 16000x1200
- max zoom: 2.0 
- min zoom: 0.25
- Zooming will not go off-screen, translation is applied to keep it on screen
- rotating allows for off-screen rotation

## Sources
All images were found on FlatIcon.com