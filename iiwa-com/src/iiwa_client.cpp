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
        boost::asio::connect(socket, endpoint);
    }

    void send(std::string message) {
        socket.send(boost::asio::buffer(message + "\nEND\n"));
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




int main(int argc, char *argv[]){


    boost::asio::io_service io;
    Client client(io, "127.0.0.1", std::to_string(6066));

    for(int i = 0; i < 100; i++){
        client.send("Hello " + std::to_string(i));
        boost::this_thread::sleep_for(boost::chrono::milliseconds(2));
    }
    std::cout<< "finished" << std::endl;

}
