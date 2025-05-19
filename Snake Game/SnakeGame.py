import tkinter as tk
import random
class SnakeGame(tk.Tk):
    def __init__(self):
        # Inherit properties from tk.Tk
        super().__init__()
        self.title("Snake Game")
        # Set frame size
        self.geometry("1000x800")
        self.resizable(False, False)
        # Set variables for the game
        self.width = 800
        self.height = 800
        self.cell_size = 20
        self.snake_direction = 'Right'
        self.snake = [(100, 100), (80, 100), (60, 100)]
        self.game_over = False
        self.food = self.create_food()
        self.score = 0
        self.speed = 140
        self.time = 0
        # Initialize frames and canvas
        self.menu_frame = tk.Frame(self)
        self.menu_frame.pack(fill="both", expand=True)
        self.game_frame = tk.Frame(self)
        self.canvas = tk.Canvas(self.game_frame, bg='black', width=self.width + 200, height=self.height)
        self.start_game()
    # Display frame and canvas, then run the game
    def start_game(self):
        self.menu_frame.pack_forget()
        self.game_frame.pack(fill="both", expand=True)
        self.canvas.pack()
        self.setup_ui()
        self.run_game()
    # Display first ui and set key press to the change_direction function
    def setup_ui(self):
        self.update_ui()
        self.bind("<KeyPress>", self.change_direction)
    def update_ui(self):
        if self.check_crash():
            self.game_over = True
            # Announce game over
            self.canvas.create_text((self.width - 100) / 2, self.height / 2, text="Game Over", fill="red",
                                    font="Arial 100 bold")
        else:
            # Reset the canvas
            self.canvas.delete('all')
            for x, y in self.snake:
                self.canvas.create_rectangle(x, y, x + self.cell_size, y + self.cell_size, fill='green')
                self.canvas.create_oval(self.food[0], self.food[1], self.food[0] + self.cell_size, self.food[1] + self.cell_size, fill='red')
                self.canvas.create_rectangle(800, 0, 1000, 800, fill="white")
                self.canvas.create_text(900, 400, text="Score: " + str(self.score) + " food", fill="black", font="Arial 20 bold")
                self.canvas.create_text(900, 500, text="Time: " + str(self.time // 1000) + " seconds", fill="black",
                                        font="Arial 20 bold")
                self.canvas.create_text(900, 300, text="Speed: " + str(round(1000 / self.speed, 1)) + " fps", fill="black",
                                        font="Arial 20 bold")
    def check_crash(self):
        x, y = self.snake[0]
        # Check if the snake crashes any border
        if x < 0 or y > 800 or y < 0 or x > 800:
            return True
        # Check if the snake crashes itself
        elif (x, y) in self.snake[1:]:
            return True
    def run_game(self):
        if not self.game_over:
            self.move_snake()
            # Keep track of time using speed
            self.time += self.speed
            # Limit to 12.5 fps
            if self.speed > 80:
                # Keep on increasing speed over time
                self.speed = 140 - self.time // 1000
            self.after(self.speed, self.run_game)
    def move_snake(self):
        # Set the new head of the snake after it moved
        head_x, head_y = self.snake[0]
        if self.snake_direction == 'Left':
            head_x -= self.cell_size
        elif self.snake_direction == 'Right':
            head_x += self.cell_size
        elif self.snake_direction == 'Up':
            head_y -= self.cell_size
        elif self.snake_direction == 'Down':
            head_y += self.cell_size
        new_head = (head_x, head_y)
        # Add the new head along with the whole body of the snake previously if the snake eats the food
        if new_head == self.food:
            self.snake = [new_head] + self.snake
            # Recreate the food
            self.food = self.create_food()
            # Add score
            self.score += 1
        else:
            # Add the new head along with the first to the second to last part of the snake previously
            self.snake = [new_head] + self.snake[:-1]
        # Update the GUI after setting the snake
        self.update_ui()
    def change_direction(self, event):
        # List the directions
        opposite_directions = {'Left': 'Right', 'Right': 'Left', 'Up': 'Down', 'Down': 'Up'}
        if event.keysym in ['Left', 'Right', 'Up', 'Down']:
            # Check if the new direction is opposite to the current direction
            if event.keysym != opposite_directions[self.snake_direction]:
                self.snake_direction = event.keysym
    def create_food(self):
        # Randomly decide food location that is not on the snake
        x = random.randint(0, (self.width // self.cell_size) - 1) * self.cell_size
        y = random.randint(0, (self.height // self.cell_size) - 1) * self.cell_size
        food = (x, y)
        if food not in self.snake:
            return food
if __name__ == "__main__":
    # Create the game class
    game = SnakeGame()
    game.mainloop()
