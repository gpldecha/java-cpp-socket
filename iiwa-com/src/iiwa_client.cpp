#include <boost/asio.hpp>
#include <boost/thread.hpp>
#include <chrono>         // std::chrono::seconds
#include <thread>         // std::this_thread::sleep_for

void handleWrite(const boost::system::error_code&error)
{
}

class Client{

public:

    Client(boost::asio::io_service& svc, std::string const& host, std::string const& port)
        : io_service(svc), socket(io_service)
    {
        boost::asio::ip::tcp::resolver resolver(io_service);
        endpoint = resolver.resolve(boost::asio::ip::tcp::resolver::query(host, port));
    }

    void send(std::string message) {
        boost::asio::connect(this->socket, endpoint);
        socket.send(boost::asio::buffer(message));
    }

    void close(){
        socket.shutdown(boost::asio::ip::tcp::socket::shutdown_both, ec);
        boost::this_thread::sleep_for(boost::chrono::milliseconds(500));
        socket.close();
    }

public:

    boost::asio::io_service& io_service;
    boost::asio::ip::tcp::socket socket;
    boost::system::error_code ec;
     boost::asio::ip::tcp::resolver::iterator endpoint;

};

void client_thread() {
    boost::asio::io_service svc;
    Client client(svc, "127.0.0.1", std::to_string(6066));

    client.send("hello world 1\n");
    client.send("bye world 2\n");
}

void server_thread() {
    try
    {
        boost::asio::io_service io_service;
        boost::asio::ip::tcp::acceptor acceptor(io_service, boost::asio::ip::tcp::endpoint(boost::asio::ip::tcp::v4(), 6066));

        {
            boost::asio::ip::tcp::socket socket(io_service);
            acceptor.accept(socket);

            boost::asio::streambuf sb;
            boost::system::error_code ec;
            while (boost::asio::read(socket, sb, ec)) {
                std::cout << "received: '" << &sb << "'\n";

                if (ec) {
                    std::cout << "status: " << ec.message() << "\n";
                    break;
                }
            }
        }
    }
    catch (std::exception& e)
    {
        std::cerr << "Exception: " << e.what() << std::endl;
    }
}

#include <boost/thread.hpp>
/*
int main() {
    boost::thread_group tg;
    tg.create_thread(server_thread);

    boost::this_thread::sleep_for(boost::chrono::milliseconds(100));
    tg.create_thread(client_thread);

    tg.join_all();
}*/

int main(int argc, char *argv[]){


    boost::asio::io_service io;
    {
        Client client(io, "127.0.0.1", std::to_string(6066));
        client.send("Hello 1\n");
        boost::this_thread::sleep_for(boost::chrono::milliseconds(500));
        client.send("Hello 2\n");
        boost::this_thread::sleep_for(boost::chrono::milliseconds(500));
    }




   /*{
        Client client(io, "127.0.0.1", std::to_string(6066));
        client.send("Hello 2\n");
    }*/

    std::cout<< "finished" << std::endl;

}
