import tensorflow as tf
import numpy as np
import requests
import json

n_input : int = 6 # ???
n_hidden_1 : int = n_input + 2
n_hidden_2 : int = n_input - 2
n_output : int = 8 # ??? L R U  LU LD = 1 sofmtax
n_steps : int = 100
learning_rate : float = 0.01

def get_initial_car_state():
    response = requests.get(f"http://localhost/car")
    json_response = json.loads(response.text)
    sensors = np.array(json_response.get("sensors"), dtype=np.float32)
    angle   = json_response.get("angle")
    return np.hstack((sensors, angle))

# инициализация начальных показаний датчиков
start_input_values = np.array([get_initial_car_state()], dtype=np.float32)

data_in = tf.placeholder(tf.float32, [None, n_input], name="input")

w1 = tf.Variable(tf.random_normal([n_input, n_hidden_1]), dtype=tf.float32)
w2 = tf.Variable(tf.random_normal([n_hidden_1, n_hidden_2]), dtype=tf.float32)
w3 = tf.Variable(tf.random_normal([n_hidden_2, n_output]), dtype=tf.float32)
#activation(w*x)
hidden_1_out = tf.nn.sigmoid(tf.matmul(data_in, w1))
hidden_2_out = tf.nn.sigmoid(tf.matmul(hidden_1_out, w2))
data_out = tf.nn.softmax(tf.matmul(hidden_2_out, w3))

init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init)

data_in = sess.run(data_out, feed_dict={data_in: start_input_values})
for i in range(n_steps):
    X = next_step(data_in)
    data_in = sess.run(data_out, feed_dict={data_in: X})

cost = tf.squared_difference(.....)
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost)
#optimizer = tf.train.AdamOptimizer(learning_rate).minimize(cost)

# def get_initial_car_state():
#     response = requests.get(f"http://localhost/car")
#     json_response = json.loads(response.text)
#     sensors = np.array(json_response.get("sensors"))
#     angle   = json_response.get("angle")
#     return np.hstack((sensors, angle), dtype=np.float32)

def get_car_state(data_out):

    # data_out
    # key =



    response = requests.get(f"http://localhost/car?key={0}")
    json_response = json.loads(response.text)
    sensors = np.array(json_response.get("sensors"))
    angle   = json_response.get("angle")
    return np.hstack((sensors, angle), dtype=np.float32)