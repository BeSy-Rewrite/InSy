export class StatisticsScreen {
    instructions = [
        "Info Overlay (toggle with [Esc])",
        "Press [D] for debug info",
        "Press [,] to slow down time",
        "Press [.] to speed up time",
        "",
        "This is an n-Body gravitational simulator.",
        "At certain distances, floating point precision errors may occur.",
        "Controls:",
        "- Use the joystick to control throttle and direction.",
        "- Use the slider or mouse wheel to zoom in and out.",
        "- Click and drag to look around.",
        "",
        "Be careful with high time warp factors.",
        "They are only capped by your CPU performance.",
        "Go over 16 384x at your own risk!",
        "",
        "Good luck and have fun!",
        ""
    ];

    constructor(craft = null) {
        this.x = 0;
        this.y = 0;
        this.width = 500;
        this.height = 600;
        this.craft = craft;
        this.isVisible = true;

        this.lineHeight = 25;
        this.internalPosition = { x: 25, y: 40 };
    }

    setPosition(x, y) {
        this.x = x;
        this.y = y;
    }

    setCraft(craft) {
        this.craft = craft;
    }

    draw(context) {
        if (!this.isVisible || !this.craft) return; // Don't draw if not visible or no craft

        context.save();
        context.translate(this.x, this.y);

        context.font = "16px Arial";
        context.fillStyle = "rgba(0, 0, 0, 0.7)"; // Semi-transparent background
        context.fillRect(5, 5, this.width, this.height); // Background box for overlay

        context.fillStyle = "white";
        this.internalPosition = { x: 25, y: 40 };

        // Draw instructions
        for (const instruction of this.instructions) {
            this.drawLine(context, instruction);
        }

        // Display craft name
        this.drawLine(context, `Craft: ${this.craft.name}`);

        // Display status
        if (this.craft.isCrashed) {
            this.drawLine(context, `Status: Crashed`);
            this.drawLine(context, `Impact Velocity: ${this.craft.impactVelocity.toFixed(2)} m/s`);
        } else if (this.craft.isLanded) {
            this.drawLine(context, `Status: Landed on ${this.craft.getParent().name}`);
            this.drawLine(context, `Landed On: ${this.craft.landedBodies.join(', ')}`);
        } else {
            this.drawLine(context, `Status: In Flight over ${this.craft.getParent().name}`);
            this.drawLine(context, `Altitude: ${this.craft.getAltitude().toFixed(3)} km`);
            this.drawLine(context, `Velocity: ${this.craft.getSurfaceVelocity().toFixed(3)} m/s`);
        }

        context.restore();
    }
    drawLine(context, text) {
        context.fillText(text, this.internalPosition.x, this.internalPosition.y);
        this.internalPosition.y += this.lineHeight;
    }
}
