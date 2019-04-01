#include <random>
#include <thread>
#include <chrono>

int main()
{
    std::bernoulli_distribution b{0.4};
    std::default_random_engine e(std::chrono::system_clock::now().time_since_epoch().count());
    std::this_thread::sleep_for(std::chrono::seconds(4));
    return b(e);
}