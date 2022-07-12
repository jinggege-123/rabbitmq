package com.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.*;

@SpringBootTest
class SpringRabbitmqApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {

        Vector<String> vector = new Vector<>();
        Stack<String> stack = new Stack<>();
        ArrayList<Object> list = new ArrayList<>();
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    stack.add(Thread.currentThread().getName() + i);
                    System.out.println(stack.size());
                }
            }
        }, "A");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    stack.add(Thread.currentThread().getName() + i);
                }
            }
        }, "B");

        a.start();
        b.start();


        TimeUnit.SECONDS.sleep(2);
        System.out.println(stack.toString());
    }



    @Test
    public void test3() throws ExecutionException, InterruptedException {
        String s1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        System.out.println(s1);
        int i=2;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        ScheduledFuture<Integer> schedule = pool.schedule(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return i + 1;
            }
        }, 3, TimeUnit.SECONDS);
        Integer s = schedule.get();
        System.out.println(s);
        String s2 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        System.out.println(s2);
    }

    ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(String.valueOf(r.hashCode()));
            return thread;
        }
    };
    ThreadPoolExecutor executor = new ThreadPoolExecutor(2,5,3,TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20),threadFactory);


    class RunnableTask implements Runnable {
        Person p;
        RunnableTask(Person p) {
            this.p = p;
        }

        @Override
        public void run() {
            p.setId(1);
            p.setName("Runnable Task...");
        }
    }
    static class Person {
        private Integer id;
        private String name;

        public Person(Integer id, String name) {
            super();
            this.id = id;
            this.name = name;
        }
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return "Person [id=" + id + ", name=" + name + "]";
        }
    }

    @Test
    public  void runnableTest2() {
        //runnable + result
        Person p = new Person(0,"person");
        Future<Person> future2 = executor.submit(new RunnableTask(p),p);
        try {
            System.out.println("feature.get");
            Person person = future2.get();
            System.out.println(person);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
